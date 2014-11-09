package org.openmrs.module.ezvacimmunization.web.util;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.Obs;
import org.openmrs.module.ezvacimmunization.Vaccination;

/**
 * Class for view vaccination
 * 
 * @author student
 * 
 */
public class VaccinationView {
	protected List<Vaccination> vaccination = new ArrayList<Vaccination>();
	protected List<Obs> observations;

}
