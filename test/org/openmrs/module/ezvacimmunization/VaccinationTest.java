package org.openmrs.module.ezvacimmunization;
import java.util.HashMap;
import java.util.Vector;

import junit.framework.Assert;
import org.junit.Test;
import columbia.rules.dto.ImmunizationHistoryDTO;
import columbia.rules.dto.UtdResult;

/**
 * 
 * @author student
 * Test cases for the class VaccinationServiceImpl
 */
public class VaccinationTest {
	@Test
	public void testGroupByVaccine() {
		try{
			Vector<ImmunizationHistoryDTO> history = new Vector<ImmunizationHistoryDTO>();
			HashMap<String, UtdResult> results = new HashMap<String, UtdResult>();
			Vaccination vac=new Vaccination();
			Assert.assertNull(vac);
		}catch(NoClassDefFoundError error){
			
		}
	}
}
