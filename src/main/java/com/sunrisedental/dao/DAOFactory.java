package com.sunrisedental.dao;

import com.sunrisedental.dao.impl.*;

/**
 * Factory Pattern: DAOFactory
 * Decouples service layer from concrete DAO implementations.
 */
public class DAOFactory {

    private static final UserDAO userDAO = new UserDAOImpl();
    private static final PatientDAO patientDAO = new PatientDAOImpl();
    private static final DentistDAO dentistDAO = new DentistDAOImpl();
    private static final TreatmentDAO treatmentDAO = new TreatmentDAOImpl();
    private static final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private static final BillDAO billDAO = new BillDAOImpl();

    public static UserDAO getUserDAO() {
        return userDAO;
    }

    public static PatientDAO getPatientDAO() {
        return patientDAO;
    }

    public static DentistDAO getDentistDAO() {
        return dentistDAO;
    }

    public static TreatmentDAO getTreatmentDAO() {
        return treatmentDAO;
    }

    public static AppointmentDAO getAppointmentDAO() {
        return appointmentDAO;
    }

    public static BillDAO getBillDAO() {
        return billDAO;
    }
}
