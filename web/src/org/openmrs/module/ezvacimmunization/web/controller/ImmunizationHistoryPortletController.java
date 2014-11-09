package org.openmrs.module.ezvacimmunization.web.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptName;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ezvacimmunization.Constants;
import org.openmrs.module.ezvacimmunization.service.impl.ImmunizationServiceImpl;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.controller.PortletController;

import columbia.rules.dto.ImmunizationHistoryDTO;
import columbia.rules.dto.UtdResult;

/**
 * Portlet controller for view page on patient dashboard
 * 
 * @author student
 * 
 */
public class ImmunizationHistoryPortletController extends PortletController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	@SuppressWarnings("unused")
	private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest,
	 *      java.util.Map)
	 */
	@Override
	protected void populateModel(HttpServletRequest request,
			Map<String, Object> model) {
		Patient patient = (Patient) model.get("patient");

		ImmunizationServiceImpl imm = new ImmunizationServiceImpl();

		List<Obs> observations = imm.getObsForPatient(patient);
		List<Obs> patientObs = Context.getObsService().getObservationsByPerson(
				patient);
		/*
		 * Read encounter type from global property
		 * save encounter type read in database
		 */
		AdministrationService svc = Context.getAdministrationService();
		Boolean encounterTypeExist = null;
		String encounterTypeImmRecomm= svc.getGlobalProperty("ezvacimmunization.immunizationRecommendationEncounterType");
		List<EncounterType> encounterTypeList = Context.getEncounterService().getAllEncounterTypes();
		for(EncounterType et:encounterTypeList)
		{
			if(et.getName().equalsIgnoreCase(encounterTypeImmRecomm))
			{
				encounterTypeExist = true;
				break;
			}
			else
				encounterTypeExist = false;
		}
		if(!encounterTypeExist)
		{
			EncounterType encounterType = new EncounterType();
			encounterType.setName(encounterTypeImmRecomm);
			encounterType.setDescription("Immunization Recommendation Encounter Type");
			Context.getEncounterService().saveEncounterType(encounterType);
		}

		model.put("obs", observations);
		model.put("observation", patientObs);

		String path = OpenmrsUtil.getApplicationDataDirectory()
				+ Constants.RULES_FILE;
		log.error(path);
		// log.error(cvxFilePath);
		log.error(observations.size());

		try {
			// Date d = formatter.parse("07/11/2008");
			Date d = new Date();
			List<Vector<ImmunizationHistoryDTO>> history = imm
					.setHistory(patient);

			// sorted history list

			log.error(history.size());
			HashMap<String, UtdResult> res = new HashMap<String, UtdResult>();
			// sort history by shot number
			List<Vector<ImmunizationHistoryDTO>> historySorted = imm
					.sortImmunizationShotNumber(history);
			String cvxcode = "";
			for (Vector<ImmunizationHistoryDTO> historyDTO : historySorted) {

				res = imm.getUtdResult(patient, d, path, historyDTO);
				Date recentAdminDate = null;
				Date tmp = null;

				// for (UtdResult r : res.values()) {
				//
				// // if (h.getShotNumber() < 0
				// // || r.getNextShot() < h.getShotNumber())
				// // h.setValid(false);
				// // else
				// // h.setValid(true);
				// // if (r.getStatus().equals("C")) {
				// // r.setNextShot(0);
				// // h.setShotNumber(h.getShotNumber());
				// // h.setValid(true);
				// // }
				//
				// // if (r.getStatus().equals("NA"))
				// // h.setValid(false);
				//
				// }

				for (ImmunizationHistoryDTO h : historyDTO) {

					cvxcode = h.getCvxCode();
					if (recentAdminDate == null)
						recentAdminDate = h.getAdminDate();
					else {
						tmp = h.getAdminDate();
						if (tmp.after(recentAdminDate))
							recentAdminDate = tmp;
					}

				}
			}

			ConceptName yes = Context.getConceptService().getConcept(1065)
					.getBestShortName(Context.getLocale());
			ConceptName no = Context.getConceptService().getConcept(1066)
					.getBestShortName(Context.getLocale());

			Integer patientId = patient.getId();

			model.put("patientId", patientId);
			model.put("history", history);
			model.put("results", res.values());
			model.put("yes", yes);
			model.put("no", no);
			model.put("cvxcode", cvxcode);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String vname(List<Vector<ImmunizationHistoryDTO>> history) {
		String vaccineName = null;
		for (Vector<ImmunizationHistoryDTO> h : history) {
			for (ImmunizationHistoryDTO hh : h) {
				vaccineName = hh.getVaccineName();

			}
		}
		return vaccineName;
	}
}
