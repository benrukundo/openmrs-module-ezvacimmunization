package org.openmrs.module.ezvacimmunization.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ezvacimmunization.Constants;
import org.openmrs.module.ezvacimmunization.ImmunizationService;
import org.openmrs.module.ezvacimmunization.Vaccination;
import org.openmrs.module.ezvacimmunization.db.ImmunizationServiceDAO;
import org.openmrs.util.OpenmrsUtil;
import java.util.Iterator;
import columbia.rules.bi.RulesEngineBI;
import columbia.rules.dto.ImmunizationHistoryDTO;
import columbia.rules.dto.UtdResult;
import columbia.rules.utility.NonCollapsingStringTokenizer;

/**
 * This is the actual implementation of the {@link ImmunizationService}. This
 * calls the dao and saves the object to the database
 */
public class ImmunizationServiceImpl extends BaseOpenmrsService implements
		ImmunizationService {

	protected final Log log = LogFactory.getLog(getClass());
	private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	private ImmunizationServiceDAO immunizationServiceDAO;

	

	public ImmunizationServiceDAO getImmunizationServiceDAO() {
		return immunizationServiceDAO;
	}

	public void setImmunizationServiceDAO(
			ImmunizationServiceDAO immunizationServiceDAO) {
		this.immunizationServiceDAO = immunizationServiceDAO;
	}

	/**
	 * @see org.openmrs.module.patientsearchformlinks.service.FormLinkService#getObsByEncounter(org.openmrs.Encounter)
	 */
	public List<Obs> getObsByEncounter(Encounter encounter) {
		// TODO Auto-generated method stub
		return immunizationServiceDAO.getObsByEncounter(encounter);
	}

	public List<Encounter> getEncounterbyEncounterType(
			EncounterType encounterType) {
		// TODO Auto-generated method stub
		return immunizationServiceDAO
				.getEncounterbyEncounterType(encounterType);
	}

	public List<Patient> getPatientsbyEncounterType(EncounterType encounterType) {
		// TODO Auto-generated method stub
		return immunizationServiceDAO.getPatientsbyEncounterType(encounterType);
	}

	public List<Encounter> getPatientImmunizationRecommendationEncounter(
			Patient patient, EncounterType encounterType) {
		// TODO Auto-generated method stub
		return immunizationServiceDAO
				.getPatientImmunizationRecommendationEncounter(patient,
						encounterType);
	}

	public List<PatientIdentifier> getPatientLocation(Patient patient) {
		return immunizationServiceDAO.getPatientLocation(patient);
	}

	public List<Obs> getObsByPatient(Person person)
	{
		return immunizationServiceDAO.getObsByPatient(person);
	}

	/**
	 * @param history
	 *            the history to set
	 */
	public List<Vector<ImmunizationHistoryDTO>> setHistory(Patient patient) {

		List<Obs> obs = getObsForPatient(patient);

		return changeObsToImmunizationHistory(obs);
	}

	public List<Obs> getObsForPatient(Patient patient) {
		List<Obs> obs = Context.getObsService()
				.getObservationsByPerson(patient);
		List<Obs> vaccinations = new ArrayList<Obs>();
		for (Obs o : obs) {
			if (o.getVoided() == false) {

				if (hasCVXMappings(o.getConcept()) && (o.getVoided() == false))
					vaccinations.add(o);
				else if ((o.getValueCoded() != null)
						&& (hasCVXMappings(o.getValueCoded()))) {
					vaccinations.add(o);

				} else if (hasVaccinationDate(o.getConcept())
						&& (o.getVoided() == false))
					vaccinations.add(o);
				else if (hasSequenceNumber(o.getConcept())
						&& (o.getVoided() == false))
					vaccinations.add(o);
				// new modification from 7 June 2011
				// else if (conceptContainVaccineName(o.getConcept())
				// && (o.getVoided() == false))
				// vaccinations.add(o);

			}
		}
		return vaccinations;

	}

	public Concept getVaccineFromObs(Obs obs) {
		if (hasCVXMappings(obs.getConcept())) {
			log.error("-"
					+ obs.getConcept().getBestShortName(Context.getLocale())
							.getName());
			return obs.getConcept();
		} else if (obs.getValueCoded() != null) {
			if (hasCVXMappings(obs.getValueCoded())) {

				log.error("+"
						+ obs.getValueCoded()
								.getBestShortName(Context.getLocale())
								.getName());
				return obs.getValueCoded();
			}

		} else if (hasVaccinationDate(obs.getConcept()))
			return obs.getConcept();
		else if (hasSequenceNumber(obs.getConcept()))
			return obs.getConcept();
		// else if (conceptContainVaccineName(obs.getConcept()))
		// return obs.getConcept();

		return null;
	}

	public boolean hasVaccinationDate(Concept concept) {
		if (concept.getConceptId() == 1410)
			return true;
		return false;
	}

	public boolean hasSequenceNumber(Concept concept) {
		if (concept.getConceptId() == 1418)
			return true;
		return false;
	}

	public boolean hasCVXMappings(Concept concept) {

		for (ConceptMap conMap : concept.getConceptMappings()) {
			if (conMap.getSource().getName()
					.matches("\\w*-?\\w*\\s?\\w*CVX\\w*")) {

				return true;
			}
		}

		return false;
	}

	/**
	 * @see org.openmrs.module.ezvacimmunization#getImmunizationsByPatient(Patient)
	 */
	public List<Obs> getImmunizationsByPatient(Patient patient) {
		List<Obs> obs = getObsForPatient(patient);

		return obs;
	}

	/**
	 * @see org.openmrs.module.ezvacimmunization#getImmunizationsByPatient(Patient,Date,String)
	 */
	public HashMap<String, UtdResult> getUtdResult(Patient patient,
			Date auditDate, String path,
			Vector<ImmunizationHistoryDTO> immunizationHistory) {
		HashMap<String, UtdResult> results = new HashMap<String, UtdResult>();
		try {
			// log.error("Immunization size " + immunizationHistory.size());
			RulesEngineBI rules = new RulesEngineBI(path, auditDate,
					patient.getBirthdate(), patient.getGender(),
					immunizationHistory, true);
			ArrayList<String> vaccine = new ArrayList<String>();
			// Get list of vaccines from Global properties
			AdministrationService svc = Context.getAdministrationService();

			String s = svc.getGlobalProperty("ezvacimmunization.vaccinelist");

			String[] listVaccine = s.split(",");
			for (int i = 0; i < listVaccine.length; i++)
				vaccine.add(listVaccine[i]);

			rules.setVaccineList(vaccine);

			results = rules.fireRules();

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return results;
	}

	/**
	 * 
	 * @param value
	 * @param skipvalue
	 * @return
	 */
	boolean foundValue(Integer value, ArrayList<Integer> skipvalue) {
		for (Integer v : skipvalue) {
			if (value == v)
				return true;

		}
		return false;
	}

	/**
	 * change concept Obs to ImmunizationHistoryDTO
	 * 
	 * @param obs
	 *            the obs List
	 * @param cvxCode
	 *            the concept mappings from concept_map table
	 * @return a new immunization history
	 */
	@SuppressWarnings({ "null", "deprecation" })
	public List<Vector<ImmunizationHistoryDTO>> changeObsToImmunizationHistory(
			List<Obs> listObs) {

		List<Vector<ImmunizationHistoryDTO>> historyImmunization = new ArrayList<Vector<ImmunizationHistoryDTO>>();
		Vector<ImmunizationHistoryDTO> vImmunizationH = new Vector<ImmunizationHistoryDTO>();
		String conceptName = null;
		String cvxCode = "";
		Date adminDateValue = null;
		Integer SequenceValue = null;
		Concept concept = null;
		ArrayList<Integer> skipValue = new ArrayList<Integer>();

		Integer nextObs = null;
		String serieName = null;

		for (Obs obs : listObs) {

			@SuppressWarnings("deprecation")
			Integer obsGroupId = obs.getObsGroupId();

			if (obsGroupId != null) {
				boolean f = foundValue(obsGroupId, skipValue);

				if (f == false && obs.getVoided() == false) {
					for (Obs obs1 : listObs)

					{
						nextObs = obs1.getObsGroupId();
						skipValue.add(obsGroupId);

						if (nextObs == obsGroupId) {

							if (obs1.getConcept().getConceptId() == 1418)
								SequenceValue = obs1.getValueNumeric()
										.intValue();
							else if (obs1.getConcept().getConceptId() == 1410)
								adminDateValue = obs1.getValueDatetime();

							else if (obs1.getConcept().getConceptId() == 984) {
								concept = getVaccineFromObs(obs1);

								conceptName = concept.getBestShortName(
										Context.getLocale()).getName();
								serieName = conceptName;

								if (conceptName == null) {
									conceptName = concept.getDisplayString();

								}

								if (hasCVXMappings(concept)) {
									cvxCode = getCVXCode(concept);

								}
							}

						}

					}

					ImmunizationHistoryDTO i = new ImmunizationHistoryDTO();

					i.setCvxCode(cvxCode);

					conceptName = MapCvxCodeToSeriesName(cvxCode);

					/*
					 * Check combined vaccines, combined vaccines are separated
					 * by - character like DTAP-HEPB-HIB split combined vaccines
					 * and store separate value in an ArrayList Retrieve value
					 * from ArrayList
					 */

					if (conceptName.contains("-")) {
						NonCollapsingStringTokenizer st = new NonCollapsingStringTokenizer(
								conceptName, "-");

						List<String> combinedVaccine = new ArrayList<String>();

						while (st.hasMoreTokens()) {
							combinedVaccine.add(st.nextToken());

						}
						// Iterate through the ArrayList and retrieve values
						for (String v : combinedVaccine) {
							ImmunizationHistoryDTO im = new ImmunizationHistoryDTO();
							im.setCvxCode(cvxCode);
							im.setVaccineName(v);
							im.setAdminDate(adminDateValue);

							if (SequenceValue != null)
								im.setShotNumber(SequenceValue);
							else {
								im.setShotNumber(-1);
								// im.setValid(false);
							}
							im.setCatchup(false);
							im.setEncounterId(obs.getEncounter()
									.getEncounterId());
							vImmunizationH.add(im);
							SequenceValue = null;
						}
					}

					// Not combined vaccines

					else {
						i.setCvxCode(cvxCode);

						conceptName = MapCvxCodeToSeriesName(cvxCode);

						if (conceptName.equals("Not found"))
							conceptName = concept.getBestShortName(
									Context.getLocale()).getName()
									+ " Not Mapped correctly or with a wrong cvxcode in CSV file!";

						i.setVaccineName(conceptName);
						i.setAdminDate(adminDateValue);

						if (SequenceValue != null)
							i.setShotNumber(SequenceValue);
						// else {
						// i.setShotNumber(-1);
						// // i.setValid(false);
						// }
						// // test if vaccine is invalid then don't increment
						// shot
						// if (!i.isValid())
						// i.setShotNumber(0);

						i.setCatchup(false);
						i.setEncounterId(obs.getEncounter().getEncounterId());
						vImmunizationH.add(i);
						SequenceValue = null;

					}

				}

			}
		}

		// sorting VimmunizationH

		Vector<ImmunizationHistoryDTO> sortedImmunization = new Vector<ImmunizationHistoryDTO>();
		sortedImmunization = sortImmunization(vImmunizationH);

		historyImmunization.add(sortedImmunization);
		return historyImmunization;

	}

	private String getCVXCode(Concept concept) {
		for (ConceptMap conMap : concept.getConceptMappings()) {

			if (conMap.getSource().getName()
					.matches("\\w*-?\\w*\\s?\\w*CVX\\w*"))
				return conMap.getSourceCode();
		}

		return null;
	}

	/**
	 * get the ImmunizationHistoryDTOs
	 * 
	 * @return a vector immunizationHistory
	 */

	public Vector<ImmunizationHistoryDTO> getImmunizationHistoryDTOs() {
		Vector<ImmunizationHistoryDTO> immunizationHistory = new Vector<ImmunizationHistoryDTO>();

		return immunizationHistory;
	}

	/**
	 * @see
	 */
	public Vaccination groupByVaccine(Vector<ImmunizationHistoryDTO> history,
			HashMap<String, UtdResult> results) {
		Vaccination vacc = new Vaccination();
		boolean test = false;
		if (history.size() > 0) {
			String vaccineName = history.get(0).getVaccineName();
			test = false;
			UtdResult result = new UtdResult();
			for (String s : results.keySet()) {

				if (test) {
					break;
				} else if (vaccineName.equalsIgnoreCase(s)) {
					result = results.get(s);
					String status = result.getStatus();
					test = true;
					return new Vaccination(vaccineName, history.get(0), status);
				}
			}
			return vacc;
		} else
			return null;
	}

	/**
	 * Sort the immunization by date from the recent date to the old date
	 * 
	 * @param immunizationVector
	 * @return vector of immunization
	 */
	public Vector<ImmunizationHistoryDTO> sortImmunization(
			Vector<ImmunizationHistoryDTO> immunizationVector) {

		int n = immunizationVector.size();
		boolean doMore = true;
		while (doMore) {
			n--;
			doMore = false; // assume this is our last pass over the array
			for (int i = 0; i < n; i++) {
				if (immunizationVector.get(i).getAdminDate()
						.before(immunizationVector.get(i + 1).getAdminDate())) {

					// exchange elements
					String tempCvxCode = immunizationVector.get(i).getCvxCode();
					String tempvaccineName = immunizationVector.get(i)
							.getVaccineName();
					Date tempAdminDate = immunizationVector.get(i)
							.getAdminDate();
					int tempShotNumber = immunizationVector.get(i)
							.getShotNumber();

					immunizationVector.get(i).setCvxCode(
							immunizationVector.get(i + 1).getCvxCode());
					immunizationVector.get(i).setVaccineName(
							immunizationVector.get(i + 1).getVaccineName());
					immunizationVector.get(i).setAdminDate(
							immunizationVector.get(i + 1).getAdminDate());
					immunizationVector.get(i).setShotNumber(
							immunizationVector.get(i + 1).getShotNumber());

					immunizationVector.get(i + 1).setCvxCode(tempCvxCode);
					immunizationVector.get(i + 1).setVaccineName(
							tempvaccineName);
					immunizationVector.get(i + 1).setAdminDate(tempAdminDate);
					immunizationVector.get(i + 1).setShotNumber(tempShotNumber);

					doMore = true; // after an exchange, must look again
				}
			}
		}

		return immunizationVector;

	}

	public boolean findVaccine(String vaccine,
			Vector<ImmunizationHistoryDTO> immunizationVector) {
		for (int i = 0; i < immunizationVector.size(); i++) {
			if (vaccine.equals(immunizationVector.get(i)))
				return true;

		}
		return false;
	}

	// Map cvx code to the series name
	/**
 * 
 */
	public String MapCvxCodeToSeriesName(String cvxcode) {

		String conceptName = mappedVaccine(Integer.parseInt(cvxcode));

		return conceptName;
	}

	/**
	 * Read vaccine csv file and extract all cvxcode mapped to a vaccine
	 * 
	 * @param cvxcode
	 *            of vaccine
	 * @return vaccine
	 */

	public String mappedVaccine(Integer vaccineCvxCode) {
		// reading vaccine from csv file

		// csv file containing data
		String strFile = OpenmrsUtil.getApplicationDataDirectory()
				+ Constants.VACCINE_FILE;

		Map<String, List<Integer>> vaccineMap = new HashMap<String, List<Integer>>();
		String vaccineName = null;

		try {
			BufferedReader br = new BufferedReader(new FileReader(strFile));

			String rowContent = null;
			int row = 0;

			try {
				while ((rowContent = br.readLine()) != null)

				{

					if (row > 0) {
						NonCollapsingStringTokenizer st = new NonCollapsingStringTokenizer(
								rowContent, ",");

						List<Integer> cvxcode = new ArrayList<Integer>();

						int column = 0;
						while (st.hasMoreTokens()) {
							String columnValue = st.nextToken();

							if (column == 0) {

								vaccineName = columnValue;
							} else
								cvxcode.add((Integer.parseInt(columnValue)));

							column++;
						}
						vaccineMap.put(vaccineName, cvxcode);

					}
					row++;

				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading hashmap values

		String findVaccine = null;
		for (Entry<String, List<Integer>> entry : vaccineMap.entrySet()) {

			if (entry.getValue().contains(new Integer(vaccineCvxCode))) {
				findVaccine = entry.getKey();
				break;
			} else
				findVaccine = "Not found";

		}
		return findVaccine;
	}

	/**
	 * 
	 * @param immunizationVector
	 * @return
	 */
	public List<Vector<ImmunizationHistoryDTO>> sortImmunizationShotNumber(
			List<Vector<ImmunizationHistoryDTO>> immunizationVector) {
		String vaccineToTest;

		boolean find = false;

		Vector<ImmunizationHistoryDTO> testedVaccine = new Vector<ImmunizationHistoryDTO>();
		for (Vector<ImmunizationHistoryDTO> historyDTO : immunizationVector) {
			for (int j = 0; j < historyDTO.size(); j++) {
				vaccineToTest = historyDTO.get(j).getVaccineName();
				find = findVaccine(vaccineToTest, historyDTO);
				int count = 0;

				for (int k = j; k < historyDTO.size(); k++) {
					if (find == false) {
						testedVaccine.add(historyDTO.get(k));
						if (historyDTO.get(k).getVaccineName()
								.equals(vaccineToTest)) {

							historyDTO.get(k).setShotNumber(Math.abs(count++));
						}
					}

				}
			}

		}
		return immunizationVector;

	}

	/**
	 * Check if string is numeric
	 * 
	 * @param input
	 * @return true if input is numeric otherwise return false
	 */
	public boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param patient
	 * @param age
	 * @param gender
	 * @param encounter_date
	 * @param immRecommendation
	 * @return observation of patient identification and recommended
	 *         immunization
	 */
	public Obs ImmunizationRecommendation(Patient patient, Date obsDate,
			ArrayList<String> immRecommendation) {

		Obs obs = new Obs();
		obs.setPerson(patient);
		obs.setObsDatetime(obsDate);
		obs.setConcept(Context.getConceptService().getConcept(160301));
		obs.setValueText(immRecommendation.toString());

		return obs;

	}

	/**
	 * 
	 * @param res
	 *            parameter that contains all recommended vaccine rules
	 * @return ArrayList of type string that contains all recommended vaccines
	 *         (series,due status, due date)
	 */

	public ArrayList<String> RecommendedVaccine(HashMap<String, UtdResult> res)

	{
		ArrayList<String> seriesStatus = new ArrayList<String>();
		

		for (UtdResult r : res.values()) {
			
			// Test if Vaccine is Due then extract it
			if (r.getStatus().equals("D")) {

				seriesStatus.add(r.getSeriesName());
				seriesStatus.add("|");
				seriesStatus.add("Due");
				seriesStatus.add("|");
				seriesStatus.add(formatter.format(r.getNextShotDate()));
				seriesStatus.add(";");

			}
			if (r.getStatus().equals("N")) {

				seriesStatus.add(r.getSeriesName());
				seriesStatus.add("|");
				seriesStatus.add("Overdue");
				seriesStatus.add("|");
				seriesStatus.add(formatter.format(r.getNextShotDate()));
				seriesStatus.add(";");

			}
		}
		return seriesStatus;

	}

	
	/**
	 * Get Encounter List by Encounter Type
	 * 
	 * @return list of encounters
	 */
	public List<Encounter> getEncounterByEncounterType() {
		ImmunizationService imsvc = Context
				.getService(ImmunizationService.class);

		// Get EncounterTypeId from the global properties
		AdministrationService svc = Context.getAdministrationService();
		String encounterTypeID = svc
				.getGlobalProperty("ezvacimmunization.encountertypeid");

		// getEncounterbyEncounterType

		EncounterType encType = Context.getEncounterService().getEncounterType(
				Integer.parseInt(encounterTypeID));
		List<Encounter> encounterList = imsvc
				.getEncounterbyEncounterType(encType);
		return encounterList;
	}

	/**
	 * 
	 * @param request
	 * @param date
	 *            encounter Date will be used as the audit date to calculate the
	 *            immunization recommendation
	 * @return return the immunization recommendation
	 */
	public ArrayList<String> getImmunizationRecommendation(
			HttpServletRequest request, Date date) {

		// Get encounter Id

		String encounterId = request.getParameter("encounterId");
		Encounter enc = Context.getEncounterService().getEncounter(
				Integer.valueOf(encounterId));

		// Get Patient
		Patient patient = enc.getPatient();

		List<Obs> observations = getObsForPatient(patient);
		List<Obs> patientObs = Context.getObsService().getObservationsByPerson(
				patient);
		List<String> vac = null;
		ArrayList<String> seriesStatus = new ArrayList<String>();

		String path = OpenmrsUtil.getApplicationDataDirectory()
				+ Constants.RULES_FILE;
		log.error(path);
		// log.error(cvxFilePath);
		log.error(observations.size());

		try {
			// Date d = formatter.parse("07/11/2008");
			// Date must be entered from encounter form this will help to
			// control past and future date for a valid vaccine
			// Date d = new Date();
			List<Vector<ImmunizationHistoryDTO>> history = setHistory(patient);

			// sorted history list
			log.error(history.size());
			HashMap<String, UtdResult> res = new HashMap<String, UtdResult>();
			// sort history by shot number
			List<Vector<ImmunizationHistoryDTO>> historySorted = sortImmunizationShotNumber(history);
			String cvxcode = "";
			for (Vector<ImmunizationHistoryDTO> historyDTO : historySorted) {

				res = getUtdResult(patient, date, path, historyDTO);
				Date recentAdminDate = null;
				Date tmp = null;

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

			// Get all Due vaccines

			seriesStatus = RecommendedVaccine(res);

			Integer patientId = patient.getId();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return seriesStatus;

	}
	
	
	/**
	 * 
	 * @param encounterId
	 * @param date
	 *            encounter Date will be used as the audit date to calculate the
	 *            immunization recommendation
	 * @return return the immunization recommendation
	 */
	public ArrayList<String> getImmunizationRecommendation(
			Integer encounterId, Date date) {

		// Get encounter Id

	
		Encounter enc = Context.getEncounterService().getEncounter(
				Integer.valueOf(encounterId));

		// Get Patient
		Patient patient = enc.getPatient();

		List<Obs> observations = getObsForPatient(patient);
		List<Obs> patientObs = Context.getObsService().getObservationsByPerson(
				patient);
		List<String> vac = null;
		ArrayList<String> seriesStatus = new ArrayList<String>();

		String path = OpenmrsUtil.getApplicationDataDirectory()
				+ Constants.RULES_FILE;
		log.error(path);
		// log.error(cvxFilePath);
		log.error(observations.size());

		try {
			// Date d = formatter.parse("07/11/2008");
			// Date must be entered from encounter form this will help to
			// control past and future date for a valid vaccine
			// Date d = new Date();
			List<Vector<ImmunizationHistoryDTO>> history = setHistory(patient);

			// sorted history list
			log.error(history.size());
			HashMap<String, UtdResult> res = new HashMap<String, UtdResult>();
			// sort history by shot number
			List<Vector<ImmunizationHistoryDTO>> historySorted = sortImmunizationShotNumber(history);
			String cvxcode = "";
			for (Vector<ImmunizationHistoryDTO> historyDTO : historySorted) {

				res = getUtdResult(patient, date, path, historyDTO);
				Date recentAdminDate = null;
				Date tmp = null;

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

			// Get all Due vaccines

			seriesStatus = RecommendedVaccine(res);

			Integer patientId = patient.getId();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return seriesStatus;

	}

	/**
	 * Get all Patient Recommendation vaccines. Method used by
	 * saveallimmunizationEncounter
	 * 
	 * @param encounter
	 * @param date
	 *            encounter date will be used as the audit date this will help
	 *            to validate vaccine recommendation
	 * @return List of recommended vaccine
	 */
	public ArrayList<String> getPatientImmunizationRecommendation(
			Encounter encounter, Date date) {

		// Get Patient
		Patient patient = encounter.getPatient();

		List<Obs> observations = getObsForPatient(patient);
		List<Obs> patientObs = Context.getObsService().getObservationsByPerson(
				patient);
		List<String> vac = null;
		ArrayList<String> seriesStatus = new ArrayList<String>();

		String path = OpenmrsUtil.getApplicationDataDirectory()
				+ Constants.RULES_FILE;
		log.error(path);
		// log.error(cvxFilePath);
		log.error(observations.size());

		try {
			// Date d = formatter.parse("07/11/2008");
			// Date d = new Date();
			List<Vector<ImmunizationHistoryDTO>> history = setHistory(patient);

			// sorted history list

			log.error(history.size());
			HashMap<String, UtdResult> res = new HashMap<String, UtdResult>();
			// sort history by shot number
			List<Vector<ImmunizationHistoryDTO>> historySorted = sortImmunizationShotNumber(history);
			String cvxcode = "";
			for (Vector<ImmunizationHistoryDTO> historyDTO : historySorted) {

				res = getUtdResult(patient, date, path, historyDTO);
				Date recentAdminDate = null;
				Date tmp = null;

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

			// Get all Due vaccines

			seriesStatus = RecommendedVaccine(res);

			Integer patientId = patient.getId();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return seriesStatus;

	}

	/**
	 * Remove duplicate item
	 * 
	 * @param list
	 */

	public List removeDuplicatedItem(ArrayList list) {
		HashSet set = new HashSet(list);
		list.clear();
		list.addAll(set);
		return list;
	}

}
