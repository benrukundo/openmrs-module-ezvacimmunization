package org.openmrs.module.ezvacimmunization;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.springframework.transaction.annotation.Transactional;

import columbia.rules.dto.ImmunizationHistoryDTO;
import columbia.rules.dto.UtdResult;

/**
 * This is the interface for the service that will load Obs for a specific
 * patient.
 * 
 * @see ImmunizationServiceImpl
 * @see ImmunizationServiceDAO
 * @see HibernateImmunizationServiceDAO
 */
@Transactional
public interface ImmunizationService {

	/**
	 * get immunization history by a specified patient
	 * 
	 * @param patient
	 *            the patient whose observation to return
	 * @return a list of Observation
	 */
	public List<Obs> getImmunizationsByPatient(Patient patient);

	/**
	 * Gets the map of results and return an historical vaccination with dates
	 * and status(valid, invalid, etc.)
	 * 
	 * @param patient
	 *            the patient whose historical vaccination
	 * @param auditDate
	 *            the date of audit
	 * @param path
	 *            the path of utdRules file witch implement the rules
	 * @return results
	 */
	// public HashMap<String, UtdResult> getUtdResult(Patient patient,
	// Date auditDate, String cvxCodeFilePath,String path,
	// Vector<ImmunizationHistoryDTO> immunizationHistory);

	public HashMap<String, UtdResult> getUtdResult(Patient patient,
			Date auditDate, String path,
			Vector<ImmunizationHistoryDTO> immunizationHistory);

	/**
	 * is used to return a list of encounter Object
	 * 
	 * @param encounter
	 *            type
	 * @return encounter
	 */

	List<Obs> getObsByEncounter(Encounter encounter);

	public List<Encounter> getEncounterbyEncounterType(
			EncounterType encounterType);

	public List<Patient> getPatientsbyEncounterType(EncounterType encounterType);

	public List<Encounter> getPatientImmunizationRecommendationEncounter(
			Patient patient, EncounterType encounterType);
	public List<PatientIdentifier> getPatientLocation(Patient patient);
	public List<Obs> getObsByPatient(Person person);
}
