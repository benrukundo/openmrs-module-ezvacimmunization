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

import org.openmrs.Encounter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 */
@SuppressWarnings("unchecked")
public class SaveCohortImmunizationRecommendationValidator implements Validator {
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {
		return Encounter.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	public void validate(Object obj, Errors e) {
		
		Encounter encounter = (Encounter) obj;
		
//		if (encounter.getLocation() == null)
//			e.rejectValue("location", "ezvacimmunization.location.empty");
		if (encounter.getProvider() == null)
			e.rejectValue("provider", "ezvacimmunization.provider.empty");
		if (encounter.getEncounterType() == null)
			e.rejectValue("encounterType", "ezvacimmunization.encounterType.empty");

		if (encounter.getEncounterDatetime() == null)
			e.rejectValue("encounterDatetime", "ezvacimmunization.date.empty");
		
		
		
	}
	
}
