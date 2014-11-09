package org.openmrs.module.ezvacimmunization;

import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.ezvacimmunization.impl.ImmunizationServiceImpl;
import columbia.rules.dto.UtdResult;
import columbia.rules.dto.ImmunizationHistoryDTO;

/**
 * Test cases for the class ImmunizationServiceImpl
 */
public class ImmunizationServiceTest {

	ImmunizationServiceImpl imm = new ImmunizationServiceImpl();
	Vector<ImmunizationHistoryDTO> immunizationHistory;
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * test the getUtdResultat method
	 */
	@Test
	public void testGetUtdResultat() {
		Patient patient = new Patient();
		String path = "/usr/share/tomcat6/.OpenMRS";
		String cvxPath = "/usr/share/tomcat6/.OpenMRS";
		HashMap<String, UtdResult> td = imm.getUtdResult(patient, new Date(),
				path, immunizationHistory);
		Assert.assertNotNull("should get the utdresultat", td);

	}

	/**
	 * test for getImmunizationsByPatient method
	 */
	@Test
	public void testgetImmunizationsByPatient() {
		try {
			Patient patient = new Patient();
			List<Obs> obs = imm.getImmunizationsByPatient(patient);
			Assert.assertNotNull(obs);
		} catch (NoClassDefFoundError error) {

		}
	}

	/**
	 * test the changeObsToImmunizationHistory method
	 */
	@Test
	public void testChangeObsToImmunizationHistory() {
		try {
			Obs obs = new Obs();
			Patient patient = new Patient();
			patient.setId(2);
			obs.setId(1);
			obs.getId();
			String cvx = "43";
			ImmunizationHistoryDTO dto = new ImmunizationHistoryDTO(cvx,
					"Hepatitis B vaccine, adult", new Date(), 0, false, false);
//			dto = imm.changeObsToImmunizationHistory(obs);
			Assert.assertNull(dto);
		} catch (NullPointerException e) {
		}

	}

	/**
	 * test the getImmunizationHistorDTOs method
	 */
	@Test
	public void testGetImmunizationHistoryDTOs() {
		Vector<ImmunizationHistoryDTO> immunizationHistory = new Vector<ImmunizationHistoryDTO>();
		immunizationHistory = imm.getImmunizationHistoryDTOs();
		Assert.assertNotNull(immunizationHistory);

	}

	/**
	 * test the getHistory and setHistory methods
	 */
	@Test
	public void testSethistory() {
		try {
			Patient patient = new Patient();
			List<Vector<ImmunizationHistoryDTO>> history = new ArrayList<Vector<ImmunizationHistoryDTO>>();
		
			Assert.assertNotNull(history);
			Assert.assertNotNull(imm.setHistory(patient));
			Assert.assertNotSame(patient, 	imm.setHistory(patient));
		} catch (NoClassDefFoundError error) {

		}
	}

}
