/**
 * 
 */
package org.openmrs.module.ezvacimmunization.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.ezvacimmunization.ImmunizationService;
import org.openmrs.module.ezvacimmunization.service.impl.ImmunizationServiceImpl;
import org.openmrs.propertyeditor.EncounterTypeEditor;
import org.openmrs.propertyeditor.FormEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.openmrs.propertyeditor.UserEditor;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author benjamin
 * 
 */
public class SaveAllImmunizationEncounterController extends
		SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	protected ImmunizationServiceImpl imm = new ImmunizationServiceImpl();
	private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	protected int count = 0;
	protected ArrayList<Integer> listCount = new ArrayList<Integer>();

	/**
	 * Allows for Integers to be used as values in input tags. Normally, only
	 * strings and lists are expected
	 * 
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
	 *      org.springframework.web.bind.ServletRequestDataBinder)
	 */
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		binder.registerCustomEditor(java.lang.Integer.class,
				new CustomNumberEditor(java.lang.Integer.class, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
				Context.getDateFormat(), true));
		binder.registerCustomEditor(EncounterType.class,
				new EncounterTypeEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(User.class, new UserEditor());
		binder.registerCustomEditor(Form.class, new FormEditor());
		binder.registerCustomEditor(Person.class, new PersonEditor());

	}

	/**
	 * The onSubmit function receives the form/command object that was modified
	 * by the input form and saves it to the db
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		Configuration configuration = new Configuration().configure();
		configuration.setProperty("hibernate.jdbc.batch_size", "50");
		configuration.setProperty("hibernate.cache.use_second_level_cache",
				"false");
		// / sessionFactory = configuration.buildSessionFactory();

		String view = getFormView();

		try {
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);

			ImmunizationService imsvc = Context
					.getService(ImmunizationService.class);
			AdministrationService svc = Context.getAdministrationService();

			// Get encounter Type ID that will serve to group patients
			String encounterTypeID = svc
					.getGlobalProperty("ezvacimmunization.encountertypeid");
			EncounterType encType = Context.getEncounterService()
					.getEncounterType(Integer.parseInt(encounterTypeID));

			// Get immunization recommendation encounter type defined in global
			// property

			String encounterTypeImmRecomm = svc
					.getGlobalProperty("ezvacimmunization.immunizationRecommendationEncounterType");

			EncounterType eType = Context.getEncounterService()
					.getEncounterType(encounterTypeImmRecomm);

			Boolean isNewEncounter = true;

			if (Context.isAuthenticated()) {

				Encounter encounter = (Encounter) command;
				int encounterId = 0;

				Person provider = encounter.getProvider();
				EncounterType encountertyp = encounter.getEncounterType();
				Location location = null;
				Date encounterDate = encounter.getEncounterDatetime();

				// Select all patients matching encounter type defined above

				List<Patient> patientEncounterList = imsvc
						.getPatientsbyEncounterType(encType);

				for (int i = 0; i < patientEncounterList.size(); i++) {

					List<PatientIdentifier> getPatientIdentifier = imsvc
							.getPatientLocation(patientEncounterList.get(i));
					for (PatientIdentifier pi : getPatientIdentifier) {
						location = pi.getLocation();

					}
					List<Encounter> patientEncounterImmunizationList = imsvc
							.getPatientImmunizationRecommendationEncounter(
									patientEncounterList.get(i), eType);

					if (patientEncounterImmunizationList.isEmpty()) {
						encounter = new Encounter();
						encounter.setPatient(patientEncounterList.get(i));
						// encounter.setPatientId(patientId);
						encounter.setProvider(provider);
						encounter.setLocation(location);
						encounter.setEncounterDatetime(encounterDate);
						encounter.setEncounterType(encountertyp);
						Context.getEncounterService().saveEncounter(encounter);
						// flush and clear memory
						// if (i % 50 == 0) {
						// Context.flushSession();
						// Context.clearSession();
						// }
						// save observation
						saveObservation(encounter, request);

						// flush and clear memory
						if (i % 50 == 0) {

							Context.flushSession();
							Context.clearSession();
						}

					}

					else {
						int counter = 0;
						int listSize = patientEncounterImmunizationList.size();
						for (Encounter e : patientEncounterImmunizationList) {
							counter++;
							if (counter == listSize) {
								encounterId = e.getEncounterId();
								// encounterDate = e.getEncounterDatetime();

								e.setProvider(provider);
								e.setLocation(location);
								e.setEncounterDatetime(encounterDate);
								e.setEncounterType(encountertyp);

								// encounter = Context.getEncounterService()
								// .getEncounter(encounterId);
								Context.getEncounterService().saveEncounter(e);
								// flush and clear memory
//								 if (i % 50 == 0) {
//								 Context.flushSession();
//								 Context.clearSession();
//								 }
								// Save observations
								saveObservation(e, request);
								// isNewEncounter = false;

								// flush and clear memory
								if (i % 50 == 0) {
									Context.flushSession();
									Context.clearSession();
								}

								break;
							}

						}
					}
					count = count + i;
					listCount.add(count);

				}

			}

			view = getSuccessView();
			// view = view + "?encounterId=" + encounter.getEncounterId();

		} finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		}

		return new ModelAndView(new RedirectView(view));

	}

	/**
	 * This is called prior to displaying a form for the first time. It tells
	 * Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request)
			throws ServletException {

		Encounter encounter = new Encounter();
		AdministrationService svc = Context.getAdministrationService();
		String encounterTypeImmRecomm = svc
				.getGlobalProperty("ezvacimmunization.immunizationRecommendationEncounterType");

		EncounterType encounterType = Context.getEncounterService()
				.getEncounterType(encounterTypeImmRecomm);
		encounter.setEncounterType(encounterType);

		return encounter;
	}

	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	@SuppressWarnings("unused")
	protected Map<String, Object> referenceData(HttpServletRequest request,
			Object command, Errors error) throws Exception {

		Encounter encounter = (Encounter) command;
		AdministrationService svc = Context.getAdministrationService();
		String encounterTypeImmRecomm = svc
				.getGlobalProperty("ezvacimmunization.immunizationRecommendationEncounterType");

		EncounterType eType = Context.getEncounterService().getEncounterType(
				encounterTypeImmRecomm);

		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		if (Context.isAuthenticated()) {

			EncounterService es = Context.getEncounterService();

			dataMap.put("encounterTypes", es.getAllEncounterTypes());
			dataMap.put("encounterType", eType);
			dataMap.put("count", count);
			dataMap.put("listCount", listCount);
			// List<Patient> patientList =
			// .getPatientsEncounterByEncounterType();

			// dataMap.put("numberOfPatients", patientList);
		}

		return dataMap;
	}

	protected void saveObservation(Encounter encounter,
			HttpServletRequest request) {

		// Save Observations
		Date date = encounter.getEncounterDatetime();
		String vacRecomm = imm.getPatientImmunizationRecommendation(encounter,
				date).toString();
		// Remove characters [ ] , from recommendation vaccines
		// string

		String immRecomm = vacRecomm.replaceAll("[\\[\\],]", "");
		Integer encounterId = encounter.getEncounterId();
		List<Obs> patientObsList = new ArrayList<Obs>();
		patientObsList = Context.getObsService().getObservations(
				String.valueOf(encounterId));
		int obsSize = patientObsList.size();
		
		if (obsSize > 1) {
			int count = 0;
			for (Obs ob : patientObsList) {
				count++;
				if (count == 1)
					continue;
				else
					Context.getObsService().voidObs(ob,
							"old immunization recommendation");
			}
		}
		if (!patientObsList.isEmpty()) {
			for (Obs obs : patientObsList) {

				Context.clearSession();
				// Context.closeSession();

				obs.setEncounter(encounter);
				obs.setPerson(encounter.getPatient());
				obs.setLocation(encounter.getLocation());
				obs.setObsDatetime(date);
				obs.setConcept(Context.getConceptService().getConcept(160301));
				obs.setValueText(immRecomm);
				Context.getObsService().saveObs(obs,
						"new Immunization recommendation");

				break;

			}
		} else {
			Context.clearSession();
			// Context.closeSession();
			Obs obs = new Obs();
			obs.setEncounter(encounter);
			obs.setPerson(encounter.getPatient());
			obs.setLocation(encounter.getLocation());
			obs.setObsDatetime(date);
			obs.setConcept(Context.getConceptService().getConcept(160301));
			obs.setValueText(immRecomm);
			Context.getObsService().saveObs(obs,
					"new Immunization recommendation");

		}

	}
}
