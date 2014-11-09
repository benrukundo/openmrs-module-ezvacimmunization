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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.ezvacimmunization.Constants;
import org.openmrs.module.ezvacimmunization.service.impl.ImmunizationServiceImpl;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import columbia.rules.utility.NonCollapsingStringTokenizer;

public class VaccineListController extends ParameterizableViewController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		/*
		 * reading vaccine mapped to cvxcode from csv file
		 */

		String strFile = OpenmrsUtil.getApplicationDataDirectory()
				+ Constants.VACCINE_FILE;
		ImmunizationServiceImpl imm = new ImmunizationServiceImpl();

		String vaccineName = null;

		BufferedReader br = new BufferedReader(new FileReader(strFile));
		String rowContent = null;
		int row = 0;
		Map<String, List<String>> vaccineMap = new HashMap<String, List<String>>();

		while ((rowContent = br.readLine()) != null)

		{

			if (row > 0) {
				NonCollapsingStringTokenizer st = new NonCollapsingStringTokenizer(
						rowContent, ",");

				List<String> cvxcode = new ArrayList<String>();

				int column = 0;
				while (st.hasMoreTokens()) {

					String columnValue = st.nextToken();

					if (column == 0) {

						vaccineName = columnValue;
					} else if (imm.isInteger(columnValue)) {
						cvxcode.add(columnValue);
					}
						column++;
					
				}
				vaccineMap.put(vaccineName, cvxcode);

			}
			row++;

		}

		ConceptName yes = Context.getConceptService().getConcept(1065)
				.getBestShortName(Context.getLocale());
		ConceptName no = Context.getConceptService().getConcept(1066)
				.getBestShortName(Context.getLocale());

		model.put("no", no);
		model.put("yes", yes);
		model.put("vaccinemap", vaccineMap);

		return new ModelAndView(getViewName(), model);
	}

}
