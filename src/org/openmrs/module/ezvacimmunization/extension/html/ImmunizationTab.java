package org.openmrs.module.ezvacimmunization.extension.html;

import org.openmrs.module.web.extension.PatientDashboardTabExt;

/**
 * This class defines the links tab that will appear on the patient dash-board
 * 
 */
public class ImmunizationTab extends PatientDashboardTabExt {

	@Override
	public String getPortletUrl() {
		return "immunizationHistory";
	}

	@Override
	public String getRequiredPrivilege() {
		return null;
	}

	@Override
	public String getTabId() {
		return "ezvacimmunization";
	}

	@Override
	public String getTabName() {
		return "ezvacimmunization.patientDashboard.tab";
	}

}
