package com.sunrisedental.service;

import com.sunrisedental.dao.*;
import com.sunrisedental.dto.ReportSummaryDTO;

import java.math.BigDecimal;

public class ReportService {

    private final AppointmentDAO appointmentDAO;
    private final PatientDAO patientDAO;
    private final BillDAO billDAO;

    public ReportService() {
        this.appointmentDAO = DAOFactory.getAppointmentDAO();
        this.patientDAO = DAOFactory.getPatientDAO();
        this.billDAO = DAOFactory.getBillDAO();
    }

    public ReportService(AppointmentDAO appointmentDAO, PatientDAO patientDAO, BillDAO billDAO) {
        this.appointmentDAO = appointmentDAO;
        this.patientDAO = patientDAO;
        this.billDAO = billDAO;
    }

    public ReportSummaryDTO getManagementSummary() {
        ReportSummaryDTO summary = new ReportSummaryDTO();
        
        summary.setTotalPatients(patientDAO.findAll().size());
        summary.setTotalAppointments(appointmentDAO.countTotal());
        summary.setScheduledAppointments(appointmentDAO.countByStatus("SCHEDULED"));
        summary.setCompletedAppointments(appointmentDAO.countByStatus("COMPLETED"));
        summary.setCancelledAppointments(appointmentDAO.countByStatus("CANCELLED"));
        
        BigDecimal totalRev = billDAO.calculateTotalRevenue();
        BigDecimal todayRev = billDAO.calculateTodayRevenue();
        summary.setTotalRevenue(totalRev != null ? totalRev : BigDecimal.ZERO);
        summary.setTodayRevenue(todayRev != null ? todayRev : BigDecimal.ZERO);
        summary.setTodayAppointmentsCount(appointmentDAO.countToday());

        summary.setRevenueByDay(billDAO.getDailyRevenueReport());
        summary.setAppointmentsByDentist(billDAO.getAppointmentsByDentistReport());
        summary.setAppointmentsByTreatment(billDAO.getAppointmentsByTreatmentReport());

        return summary;
    }
}
