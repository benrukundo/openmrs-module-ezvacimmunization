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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.classic.Session;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
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
public class ImmunizationEncounterController extends SimpleFormController {

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
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#processFormSubmission(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse reponse, Object obj, BindException errors)
			throws Exception {

		Encounter encounter = (Encounter) obj;
		

		try {
			if (Context.isAuthenticated()) {
				Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
				Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);

				if (StringUtils.hasText(request.getParameter("patientId")))
					encounter.setPatient(Context.getPatientService()
							.getPatient(
									Integer.valueOf(request
											.getParameter("patientId"))));
				if (StringUtils.hasText(request.getParameter("provider")))
					encounter.setProvider(Context.getPersonService().getPerson(
							Integer.valueOf(request.getParameter("provider"))));

			}
		} finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		}

		return super.processFormSubmission(request, reponse, encounter, errors);
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

			AdministrationService svc = Context.getAdministrationService();
			String encounterTypeImmRecomm = svc
					.getGlobalProperty("ezvacimmunization.immunizationRecommendationEncounterType");

			if (Context.isAuthenticated()) {
				Encounter encounter = (Encounter) command;
				Encounter en = null;
				List<Encounter> encList;

				if (encounter.getPatient().getPatientId() != null) {

					// Get Patient

					patient = encounter.getPatient();
					Person provider = encounter.getProvider();
					EncounterType encountertyp = encounter.getEncounterType();
					Location location = encounter.getLocation();

					// Get encounter Date
					Date encounterDate = encounter.getEncounterDatetime();
					// Get Patient encounter List
					encList = Context.getEncounterService()
							.getEncountersByPatient(patient);
					// Get only encounter with the immunization recommendation
					EncounterType eType = Context.getEncounterService()
							.getEncounterType(encounterTypeImmRecomm);
					// New encounter list that will store encounter with
					// immunization recommendation
					List<Encounter> listEncounter = new ArrayList<Encounter>();
					for (Encounter e : encList) {
						if (e.getEncounterType().getName()
								.equals(eType.getName())) {
							listEncounter.add(e);
						}
					}

					// New Encounter override previous encounter information

					int encounterSize = listEncounter.size();

					
					if (listEncounter.isEmpty()) {
						en = new Encounter();
						en.setPatient(patient);
						en.setProvider(provider);
						en.setLocation(location);
						en.setEncounterDatetime(encounterDate);
						EncounterType encounterType = Context
								.getEncounterService().getEncounterType(
										encounterTypeImmRecomm);
						en.setEncounterType(encounterType);
						Context.getEncounterService().saveEncounter(en);
						encounter = Context.getEncounterService().getEncounter(
								en.getEncounterId());
					} else {
						
						int count = 0;
						for (Encounter enc : listEncounter) {
							count++;
							if (count == encounterSize) {
								enc.setPatient(patient);
								enc.setProvider(provider);
								enc.setLocation(location);
								enc.setEncounterDatetime(encounterDate);
								EncounterType encounterType = Context
										.getEncounterService()
										.getEncounterType(
												encounterTypeImmRecomm);
								enc.setEncounterType(encounterType);
								Context.getEncounterService()
										.saveEncounter(enc);
								encounter = Context.getEncounterService()
										.getEncounter(enc.getEncounterId());
								break;
							}
						}
					}
				}
				view = getSuccessView();
				view = view + "?encounterId=" + encounter.getEncounterId();

			}
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

		Encounter encounter = null;
		List<Encounter> encounterList = null;
		List<Encounter> immEncounterList = new ArrayList<Encounter>();

		AdministrationService svc = Context.getAdministrationService();
		String encounterTypeImmRecomm = svc
				.getGlobalProperty("ezvacimmunization.immunizationRecommendationEncounterType");

		if (Context.isAuthenticated()) {
			EncounterService es = Context.getEncounterService();
			Boolean isNewEncounter = true;

			try {
				if (request.getParameter("patientId") != null
						&& !request.getParameter("patientId").equals("")) {

					Integer PatientId = Integer.parseInt(request
							.getParameter("patientId"));
					Patient patient = Context.getPatientService().getPatient(
							PatientId);
					encounterList = es.getEncountersByPatientId(PatientId);

					// Get all immunization encounter type
					for (Encounter enc : encounterList) {
						if (enc.getEncounterType().getName()
								.equalsIgnoreCase(encounterTypeImmRecomm)) {

							encounter = es.getEncounter(enc.getEncounterId());
							// immEncounterList.add(encounter);
							isNewEncounter = false;
							break;
						}

					}
					if (isNewEncounter) {
						encounter = new Encounter();
						EncounterType encounterType = Context
								.getEncounterService().getEncounterType(
										encounterTypeImmRecomm);
						encounter.setEncounterType(encounterType);

					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
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

		HashMap<String, Object> dataMap = new HashMap<String, Object>();

		int PatientId;

		if (Context.isAuthenticated()) {
			EncounterService es = Context.getEncounterService();

			dataMap.put("encounterTypes", es.getAllEncounterTypes());
		}

		if (request.getParameter("patientId") != null
				&& !request.getParameter("patientId").equals("")) {

			PatientId = Integer.parseInt(request.getParameter("patientId"));
			Patient patient = Context.getPatientService().getPatient(PatientId);
			EncounterType encType = Context.getEncounterService()
					.getEncounterType("Immunization");

			List<Encounter> encounterList = Context.getEncounterService()
					.getEncountersByPatientId(PatientId);
			int encounterId = 0;
			int encounterListSize = 0;

			for (Encounter enc : encounterList) {
				if (enc.getEncounterType().getName()
						.equalsIgnoreCase("Immunization")) {
					encounterListSize++;
					encounterId = enc.getEncounterId();
					break;
				}
			}

			dataMap.put("patientId", PatientId);
			dataMap.put("encounterImmunizationNumber", encounterListSize);
			dataMap.put("encounterid", encounterId);
			dataMap.put("defaultDate", formatter.format(new Date()));

		}

		return dataMap;
	}

}
