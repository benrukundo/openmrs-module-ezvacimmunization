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
package org.openmrs.module.ezvacimmunization.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.module.ezvacimmunization.ImmunizationService;

/**
 * This is the data-access-object interface. All implementations that do the
 * actual saving must implement this interface. HibernateSimpleServiceDAO is the
 * only implementation right now and it is set on the service by the
 * /metadata/moduleApplicationContext.xml file.<br/>
 * <br/>
 * 
 * @see ImmunizationService
 * @see ImmunizationServiceImpl
 * @see HibernateImmunizationServiceDAO
 */
public interface ImmunizationServiceDAO {

	/**
	 * Gets immunization encounter type from encounter
	 * 
	 * @param encounterType
	 *            the encounter type that will be searched from all encounter
	 * @param Patient
	 *            the patient observed
	 * @return return the encounter matching the encounter_type immunization
	 */

	List<Obs> getObsByEncounter(Encounter encounter);

	List<Encounter> getEncounterbyEncounterType(EncounterType encounterType);

	List<Patient> getPatientsbyEncounterType(EncounterType encounterType);

	List<Encounter> getPatientImmunizationRecommendationEncounter(
			Patient patient, EncounterType encounterType);
	
	List<PatientIdentifier> getPatientLocation(Patient patient);
	List<Obs> getObsByPatient(Person person);

}
