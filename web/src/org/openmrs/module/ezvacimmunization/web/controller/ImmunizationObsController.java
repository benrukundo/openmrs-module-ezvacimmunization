/**
 * 
 */
package org.openmrs.module.ezvacimmunization.web.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.GNOME.Accessibility.Command;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ezvacimmunization.Constants;
import org.openmrs.module.ezvacimmunization.service.impl.ImmunizationServiceImpl;
import org.openmrs.obs.ComplexData;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.openmrs.propertyeditor.EncounterEditor;
import org.openmrs.propertyeditor.EncounterTypeEditor;
import org.openmrs.propertyeditor.FormEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.OrderEditor;
import org.openmrs.propertyeditor.PatientEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.openmrs.propertyeditor.UserEditor;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

import columbia.rules.dto.ImmunizationHistoryDTO;
import columbia.rules.dto.UtdResult;

/**
 * @author df
 * 
 */
public class ImmunizationObsController extends SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	protected ImmunizationServiceImpl imm = new ImmunizationServiceImpl();
	private int status;

	public ImmunizationObsController() {
		setCommandClass(Obs.class);
		setCommandName("obs");
	}

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
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(java.lang.Boolean.class,
				new CustomBooleanEditor(true)); // allow for an empty boolean
												// value
		binder.registerCustomEditor(Person.class, new PersonEditor());
		binder.registerCustomEditor(Order.class, new OrderEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Encounter.class, new EncounterEditor());
		binder.registerCustomEditor(Drug.class, new DrugEditor());
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

		if (Context.isAuthenticated()) {
			Obs obs = (Obs) command;

			// redirect to the main encounter page
			if (obs.getEncounter() != null) {

				ObsService os = Context.getObsService();
				EncounterService es = Context.getEncounterService();

				String obsId = request.getParameter("obsId");
				String encounterId = request.getParameter("encounterId");

				List<Obs> patientObsList = new ArrayList<Obs>();
				patientObsList = Context.getObsService().getObservations(
						encounterId);
				int obsSize = patientObsList.size();

				if (StringUtils.hasText(encounterId)) {
					Encounter e = es.getEncounter(Integer.valueOf(encounterId));
					Patient patient = e.getPatient();
					Integer patientId = patient.getPatientId();
					Date date = e.getEncounterDatetime();

					String vacRecomm = imm.getImmunizationRecommendation(
							request, date).toString();
					// Remove characters [ ] , from recommendation vaccines
					// string

					String immRecomm = vacRecomm.replaceAll("[\\[\\],]", "");
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
					if (patientObsList.isEmpty()) {
						Obs ob = new Obs();
						ob.setEncounter(e);
						ob.setPerson(e.getPatient());
						ob.setLocation(e.getLocation());
						ob.setObsDatetime(e.getEncounterDatetime());
						ob.setConcept(Context.getConceptService().getConcept(
								160301));
						ob.setValueText(immRecomm);
						os.saveObs(ob,null);
					} else {
						for (Obs ob : patientObsList) {
							
							
							ob.setEncounter(e);
							ob.setPerson(e.getPatient());
							ob.setLocation(e.getLocation());
							ob.setObsDatetime(e.getEncounterDatetime());
							ob.setConcept(Context.getConceptService()
									.getConcept(160301));
							ob.setValueText(immRecomm);
							os.saveObs(ob, "update immunization recommendation");
							break;
						}
					}

					String view = getSuccessView() + "?patientId=" + patientId;
					return new ModelAndView(new RedirectView(view));
				}
			}
		}

		return showForm(request, response, errors);
	}

	/**
	 * This is called prior to displaying a form for the first time. It tells
	 * Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request)
			throws ServletException {

		Obs obs = null;

		// String ArrayList for storing immunization recommendation
		ArrayList<String> seriesStatus = new ArrayList<String>();

		if (Context.isAuthenticated()) {
			ObsService os = Context.getObsService();
			EncounterService es = Context.getEncounterService();

			// String obsId = request.getParameter("obsId");

			String encounterId = request.getParameter("encounterId");

			List<Obs> patientObsList = new ArrayList<Obs>();
			patientObsList = Context.getObsService().getObservations(
					encounterId);

			if (StringUtils.hasText(encounterId)) {
				Encounter e = es.getEncounter(Integer.valueOf(encounterId));
				Date date = e.getEncounterDatetime();
				String vacRecomm = imm.getImmunizationRecommendation(request,
						date).toString();
				// Remove characters [ ] , from recommendation vaccines string

				String immRecomm = vacRecomm.replaceAll("[\\[\\],]", "");
				for (Obs ob : patientObsList) {

					ob.setEncounter(e);
					ob.setPerson(e.getPatient());
					ob.setLocation(e.getLocation());
					ob.setObsDatetime(e.getEncounterDatetime());
					ob.setConcept(Context.getConceptService()
							.getConcept(160301));
					ob.setValueText(immRecomm);
					obs = Context.getObsService().getObs(ob.getObsId());
					break;
				}

			}
		}

		return obs;

	}

	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	protected Map<String, Object> referenceData(HttpServletRequest request,
			Object command, Errors error) throws Exception {

		Obs obs = (Obs) command;

		EncounterService es = Context.getEncounterService();

		String encounterId = request.getParameter("encounterId");
		Encounter e = es.getEncounter(Integer.valueOf(encounterId));
		Date date = e.getEncounterDatetime();

		Map<String, Object> map = new HashMap<String, Object>();

		if (Context.isAuthenticated()) {

			ConceptName immunizationRecommConcept = Context.getConceptService()
					.getConcept(160301).getName(Context.getLocale());

			ArrayList<String> vacRecomm = imm.getImmunizationRecommendation(
					request, date);

			map.put("immunizationRecommConcept", immunizationRecommConcept);
			map.put("encounter", e);
			map.put("recommendedVac", vacRecomm);

		}

		return map;
	}

}
