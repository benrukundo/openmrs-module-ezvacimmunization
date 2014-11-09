package org.openmrs.module.ezvacimmunization;

import columbia.rules.dto.ImmunizationHistoryDTO;

/**
 * class bean for Vaccination 
 * This class holds the history and the status of vaccine that has been given to the system to process.  
 * @author student
 * 
 */
public class Vaccination {

	private String vaccineName;
	private ImmunizationHistoryDTO history;
	private String status;

	public Vaccination(String vaccineName,ImmunizationHistoryDTO history ,String status) {
		setHistory(history);
		setStatus(status);
		setVaccineName(vaccineName);
	
	}
	public Vaccination() {
		
	}
	/**
	 * @return the vaccineName
	 */
	public String getVaccineName() {
		return vaccineName;
	}
	/**
	 * @param vaccineName the vaccineName to set
	 */
	public void setVaccineName(String vaccineName) {
		this.vaccineName = vaccineName;
	}
	/**
	 * @return the immunization history
	 */
	public ImmunizationHistoryDTO getHistory() {
		return history;
	}
	/**
	 * @param history the history to set
	 */
	public void setHistory(ImmunizationHistoryDTO history) {
		this.history = history;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


}
