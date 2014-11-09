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
package org.openmrs.module.ezvacimmunization.db.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.module.ezvacimmunization.ImmunizationService;
import org.openmrs.module.ezvacimmunization.db.ImmunizationServiceDAO;
import org.openmrs.module.ezvacimmunization.service.impl.ImmunizationServiceImpl;

/**
 * The hibernate implementation of the data access object layer.
 * 
 * @see ImmunizationService
 * @see ImmunizationServiceImpl
 * @see ImmunizationServiceDAO
 */
public class ImmunizationServiceDAOImpl implements ImmunizationServiceDAO {

	private SessionFactory sessionFactory;
	protected ImmunizationServiceImpl imm = new ImmunizationServiceImpl();
	private static int size = 10000000;

	
	/**
	 * this is used to get dataentryDAO
	 * 
	 * @return dataentryDAO
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * is used to set dataentryDAO
	 * 
	 * @param dataentryDAO
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.module.patientsearchformlinks.hibernate.FormLinkDAO#getObsByEncounter(org.openmrs.Encounter)
	 */
	@SuppressWarnings("unchecked")
	public List<Obs> getObsByEncounter(Encounter encounter) {
		Session session = getSessionFactory().getCurrentSession();
		List<Obs> obsList = session.createCriteria(Obs.class)
				.add(Restrictions.eq("encounter", encounter))
				.add(Restrictions.eq("voided", false)).list();
		return obsList;
	}

	public List<Encounter> getEncounterbyEncounterType(
			EncounterType encounterType) {
		Session session = getSessionFactory().getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Encounter> encounterList = session.createCriteria(Encounter.class)
				.add(Restrictions.eq("encounterType", encounterType))
				.add(Restrictions.eq("voided", false)).list();
		return encounterList;
	}

	public List<Patient> getPatientsbyEncounterType(EncounterType encounterType) {

		Session session = getSessionFactory().getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Encounter> encounterList = session.createCriteria(Encounter.class)
				.add(Restrictions.eq("encounterType", encounterType))
				.add(Restrictions.eq("voided", false)).list();

		ArrayList<Patient> patientListDuplicate = new ArrayList<Patient>(size);
		for (Encounter encounter : encounterList) {
			patientListDuplicate.add(encounter.getPatient());
		}

		// no duplicate patient list
		@SuppressWarnings("unchecked")
		List<Patient> patientList = new ArrayList<Patient>(size);
		patientList = imm.removeDuplicatedItem(patientListDuplicate);

		return patientList;

	}

	public List<Encounter> getPatientImmunizationRecommendationEncounter(
			Patient patient, EncounterType encounterType) {
		Session session = getSessionFactory().getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Encounter> patientEncounterList = session
				.createCriteria(Encounter.class)
				.add(Restrictions.eq("patient", patient))
				.add(Restrictions.eq("encounterType", encounterType))
//				.add(Restrictions.eq("encounterDatetime", encounterDate))
				.add(Restrictions.eq("voided", false)).list();

		List<Encounter> le = new ArrayList<Encounter>();
		for (Encounter e : patientEncounterList) {
			le.add(e);
			break;
		}

		return le;
	}

	public List<PatientIdentifier> getPatientLocation(Patient patient) {
		Session session = getSessionFactory().getCurrentSession();
		@SuppressWarnings("unchecked")
		List<PatientIdentifier> patientLocation = session
				.createCriteria(PatientIdentifier.class)
				.add(Restrictions.eq("patient", patient))
				.add(Restrictions.eq("voided", false)).list();

		return patientLocation;
	}
	
	public List<Obs> getObsByPatient(Person person){
		Session session = getSessionFactory().getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Obs> obsList = session
				.createCriteria(Obs.class)
				.add(Restrictions.eq("person", person))
				.add(Restrictions.eq("voided", false)).list();

		return obsList;
	}
}
