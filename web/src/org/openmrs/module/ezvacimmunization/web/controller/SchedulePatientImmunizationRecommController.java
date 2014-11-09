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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ezvacimmunization.ImmunizationService;
import org.openmrs.module.ezvacimmunization.service.impl.ImmunizationServiceImpl;
import org.openmrs.web.WebConstants;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class SchedulePatientImmunizationRecommController extends
		ParameterizableViewController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	protected ImmunizationServiceImpl imm = new ImmunizationServiceImpl();

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		
		
		ImmunizationService imsvc = Context
				.getService(ImmunizationService.class);
		List<EncounterType> encounterType = new ArrayList<EncounterType>();

		encounterType = Context.getEncounterService().getAllEncounterTypes();

		// Get EncounterTypeId from the global propertie
		AdministrationService svc = Context.getAdministrationService();
		String encounterTypeID = svc
				.getGlobalProperty("ezvacimmunization.encountertypeid");

		// getEncounterbyEncounterType

		EncounterType encType = Context.getEncounterService().getEncounterType(
				Integer.parseInt(encounterTypeID));
		List<Encounter> encounterList = imsvc
				.getEncounterbyEncounterType(encType);
		long start = System.currentTimeMillis();
		
		
		CohortService cs = Context.getCohortService();
		

		model.put("encounterType", encounterType);
		model.put("encounterListSize", encounterList.size());
		long end = System.currentTimeMillis();
		model.put("timeProcess", start);
		model.put("timeEnd", end);
		model.put("cohortlist", cs.getAllCohortDefinitions());

		
	
		
		return new ModelAndView(getViewName(), model);
	}

}
