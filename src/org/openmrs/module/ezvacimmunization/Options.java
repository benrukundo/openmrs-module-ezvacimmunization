package org.openmrs.module.ezvacimmunization;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * Holds the configuration options for this module
 * @author student
 *
 */
public class Options {
	protected static Options config;

	protected String vaccineCvxCode[];

	/**
	 * The default constructor
	 */
	public Options() {
		load();
	}
	/**
	 * Gets the singleton instance of this class
	 * @return the config instance
	 */
	public static Options getCurrent() {
		if (config == null)
			config = new Options();
		return config;
	}

	/**
	 * Sets the singleton instance of this class
	 * 
	 * @param config
	 *            the config instance
	 */
	public static void setCurrent(Options config) {
		Options.config = config;
	}

	public String[] getVaccineCvxCode() {
		return vaccineCvxCode;
	}

	public void setVaccineCvxCodep(String[] vaccineCvxCode) {
		this.vaccineCvxCode = vaccineCvxCode;
	}

	/**
	 * Loads the configuration from global properties
	 */
	public void load() {
		vaccineCvxCode = loadStringOption(
				Constants.PROP_VACCINE_CVXCODE,
				Constants.DEF_VACCINE_CVXCODE);
	}

	/**
	 * Saves the configuration to global properties
	 */
	public void save() {
		saveOption(Constants.PROP_VACCINE_CVXCODE,
				vaccineCvxCode);
	}

	/**o
	 * Utility method to load a string[] option from global properties
	 * 
	 * @param name the name of the global property
	 * @param cvxCode the default value if global property is invalid
	 * @return the string[] value
	 */
	@SuppressWarnings("null")
	private static String[] loadStringOption(String name,String[] def) {
		AdministrationService svc = Context.getAdministrationService();
		String s = svc.getGlobalProperty(name);
		if (s != null || ! s.equalsIgnoreCase("")) {
			return s.split(",");
		}

		return def;
	}

	/**
	 * Utility method to save an option to global properties
	 * 
	 * @param name the name of the global property     
	 * @param value the value of the global property
	 */
	private static void saveOption(String name, Object value) {
		AdministrationService svc = (AdministrationService) Context
				.getAdministrationService();
		GlobalProperty property = svc.getGlobalPropertyObject(name);
		property.setPropertyValue(String.valueOf(value));
		svc.saveGlobalProperty(property);
	}

}
