/**
 * 
 */
package org.openmrs.module.ezvacimmunization.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.classic.Session;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.CohortService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.cohort.CohortDefinitionItemHolder;
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
public class SaveCohortImmunizationRecommendationController extends
		SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	protected ImmunizationServiceImpl imm = new ImmunizationServiceImpl();
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

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

		String view = getFormView();
		Patient patient = null;

		try {
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);

			ImmunizationService imsvc = Context
					.getService(ImmunizationService.class);

			if (Context.isAuthenticated()) {
				Encounter encounter = (Encounter) command;

				Integer cohortId = Integer.parseInt(request
						.getParameter("cohortId"));

				// String cohortname = request.getParameter("cohort");
				// Cohort cohort =
				// Context.getCohortService().getCohort(cohortname);
				//

				if (cohortId != null) {

					Cohort cohort = Context.getCohortService().getCohort(
							cohortId);
					// Get Patient

					Set<Integer> patientsId = new HashSet<Integer>();
					patientsId = cohort.getMemberIds();

					Person provider = encounter.getProvider();
					Location location = encounter.getLocation();
					Date datetoday = encounter.getEncounterDatetime();
					EncounterType encountertype = encounter.getEncounterType();
					List<Obs> listObs = new ArrayList<Obs>();

					Encounter en = null;
					Integer obsId = null;
					for (Integer patientID : patientsId) {
						Boolean found = false;
						patient = Context.getPatientService().getPatient(
								patientID);

						listObs = imsvc.getObsByPatient(patient);

						// Iterate through obs list and search for concept
						// 160301
						for (Obs obs : listObs) {
							if (obs.getConcept().getConceptId() == 160301) {

								found = true;
								en = obs.getEncounter();

								obsId = obs.getObsId();
								obs.getEncounter().setPatient(patient);
								obs.getEncounter().setProvider(provider);
								obs.getEncounter().setLocation(location);
								obs.getEncounter().setEncounterDatetime(
										datetoday);
								obs.getEncounter().setEncounterType(
										encountertype);
							
								Context.getEncounterService().saveEncounter(
										obs.getEncounter());
								

								saveObservation(obs.getEncounter(), obsId,
										request);
								Context.flushSession();
								Context.clearSession();

								break;
							}
						}
						

						if (!found) {
							encounter = new Encounter();

							encounter.setPatient(patient);
							encounter.setProvider(provider);
							encounter.setLocation(location);
							encounter.setEncounterDatetime(datetoday);
							encounter.setEncounterType(encountertype);
							Context.getEncounterService().saveEncounter(
									encounter);

							Context.clearSession();

							// Save Observation
							Obs ob = new Obs();
							ob.setEncounter(encounter);
							ob.setPerson(encounter.getPatient());
							ob.setLocation(encounter.getLocation());
							ob.setObsDatetime(encounter.getEncounterDatetime());

							ob.setConcept(Context.getConceptService()
									.getConcept(160301));

							String vacRecomm = imm
									.getPatientImmunizationRecommendation(
											encounter,
											encounter.getEncounterDatetime())
									.toString();
							// Remove characters [ ] , from recommendation
							// vaccines
							// string

							String immRecomm = vacRecomm.replaceAll(
									"[\\[\\],]", "");
							ob.setValueText(immRecomm);
							Context.getObsService().saveObs(ob, null);
							// flush and clear session

							Context.flushSession();
							Context.clearSession();

						}
						
					}
				}

			}
			view = getSuccessView();
		}

		finally {
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

		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		if (Context.isAuthenticated()) {

			EncounterService es = Context.getEncounterService();

			CohortService cs = Context.getCohortService();
			// List<CohortDefinitionItemHolder> listCohortdefinition =
			// cs.getAllCohortDefinitions();

			dataMap.put("encounterTypes", es.getAllEncounterTypes());

			dataMap.put("cohortlist", cs.getAllCohorts());

		}

		return dataMap;

	}

	protected void saveObservation(Encounter encounter, Integer obsId,
			HttpServletRequest request) {

		// Save Observations
		Date date = encounter.getEncounterDatetime();
		String vacRecomm = imm.getPatientImmunizationRecommendation(encounter,
				date).toString();
		// Remove characters [ ] , from recommendation vaccines
		// string

		String immRecomm = vacRecomm.replaceAll("[\\[\\],]", "");
		Integer encounterId = encounter.getEncounterId();
		

		Context.clearSession();
		// Context.closeSession();
		Obs obs = Context.getObsService().getObs(obsId);
		obs.setEncounter(encounter);
		obs.setPerson(encounter.getPatient());
		obs.setLocation(encounter.getLocation());
		obs.setObsDatetime(date);
		obs.setConcept(Context.getConceptService().getConcept(160301));
		obs.setValueText(immRecomm);
		Context.getObsService().saveObs(obs, "new Immunization recommendation");
		
	}
}
