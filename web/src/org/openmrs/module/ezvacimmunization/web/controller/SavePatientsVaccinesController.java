/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ezvacimmunization.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.cohort.CohortDefinition;
import org.openmrs.cohort.CohortDefinitionItemHolder;
import org.openmrs.module.ezvacimmunization.ImmunizationService;
import org.openmrs.module.ezvacimmunization.service.impl.ImmunizationServiceImpl;
import org.openmrs.report.EvaluationContext;
import org.openmrs.web.WebConstants;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

public class SavePatientsVaccinesController extends
		ParameterizableViewController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	protected ImmunizationServiceImpl imm = new ImmunizationServiceImpl();

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

	
	
	
		HttpSession httpSession = request.getSession();
		Map<String, Object> model = new HashMap<String, Object>();
		MessageSourceAccessor msa = getMessageSourceAccessor();
		String success = "";
		ImmunizationService imsvc = Context
				.getService(ImmunizationService.class);
		List<EncounterType> encounterType = new ArrayList<EncounterType>();

		ObsService os = Context.getObsService();
		encounterType = Context.getEncounterService().getAllEncounterTypes();

		// Get EncounterTypeId from the global properties
		AdministrationService svc = Context.getAdministrationService();
		String encounterTypeID = svc
				.getGlobalProperty("ezvacimmunization.encountertypeid");

		// getEncounterbyEncounterType
		boolean found = false;

		EncounterType encType = Context.getEncounterService().getEncounterType(
				Integer.parseInt(encounterTypeID));
		List<Encounter> encounterList = imsvc
				.getEncounterbyEncounterType(encType);

		Integer listSize = encounterList.size();
		int count = 0;
		for (Encounter encounter : encounterList) {
			count = 0;
			Date date = encounter.getEncounterDatetime();

			String vacRecomm = imm.getImmunizationRecommendation(
					encounter.getEncounterId(), date).toString();
			// Remove characters [ ] , from recommendation vaccines
			// string

			String immRecomm = vacRecomm.replaceAll("[\\[\\],]", "");

			List<Obs> listObs = imsvc.getObsByEncounter(encounter);
			if (!listObs.isEmpty()) {
				for (Obs obs : listObs) {
					count++;
					// Test if obs contains immunization recommendation concept
					// id 160301

					if (obs.getConcept().getConceptId() == 160301){
						found = true;
						break;
					}

				}
				if (!found) {
					Obs ob = new Obs();
					ob.setEncounter(encounter);
					ob.setPerson(encounter.getPatient());
					ob.setLocation(encounter.getLocation());
					ob.setObsDatetime(encounter.getEncounterDatetime());
					ob.setConcept(Context.getConceptService()
							.getConcept(160301));
					ob.setValueText(immRecomm);
					os.saveObs(ob, "New encounter Immunization");
//					// flush and clear memory
				//	if (count % 50 == 0) {

						Context.flushSession();
						Context.clearSession();
						
					//}
				} else {
					Obs ob = new Obs();
					ob.setEncounter(encounter);
					ob.setPerson(encounter.getPatient());
					ob.setLocation(encounter.getLocation());
					ob.setObsDatetime(encounter.getEncounterDatetime());
					ob.setConcept(Context.getConceptService()
							.getConcept(160301));
					ob.setValueText(immRecomm);
					os.saveObs(ob, "New encounter Immunization");
//					// flush and clear memory
					//if (count % 50 == 0) {

						Context.flushSession();
						Context.clearSession();
						
					//}
				}

				if (!success.equals(""))
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
							success);

				model.put("encounterListSize", encounterList.size());

				
				

			}

		}
		return new ModelAndView(getViewName(), model);
	}
}
