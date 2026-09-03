package com.sunrisedental.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object: ReportSummaryDTO
 * Aggregates analytical data for clinic management reports and dashboard cards.
 */
public class ReportSummaryDTO {
    private int totalPatients;
    private int totalAppointments;
    private int completedAppointments;
    private int scheduledAppointments;
    private int cancelledAppointments;
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private int todayAppointmentsCount;
    
    // Breakdown data
    private List<Map<String, Object>> revenueByDay;
    private List<Map<String, Object>> appointmentsByDentist;
    private List<Map<String, Object>> appointmentsByTreatment;

    public ReportSummaryDTO() {}

    public int getTotalPatients() { return totalPatients; }
    public void setTotalPatients(int totalPatients) { this.totalPatients = totalPatients; }

    public int getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; }

    public int getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(int completedAppointments) { this.completedAppointments = completedAppointments; }

    public int getScheduledAppointments() { return scheduledAppointments; }
    public void setScheduledAppointments(int scheduledAppointments) { this.scheduledAppointments = scheduledAppointments; }

    public int getCancelledAppointments() { return cancelledAppointments; }
    public void setCancelledAppointments(int cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }

    public int getTodayAppointmentsCount() { return todayAppointmentsCount; }
    public void setTodayAppointmentsCount(int todayAppointmentsCount) { this.todayAppointmentsCount = todayAppointmentsCount; }

    public List<Map<String, Object>> getRevenueByDay() { return revenueByDay; }
    public void setRevenueByDay(List<Map<String, Object>> revenueByDay) { this.revenueByDay = revenueByDay; }

    public List<Map<String, Object>> getAppointmentsByDentist() { return appointmentsByDentist; }
    public void setAppointmentsByDentist(List<Map<String, Object>> appointmentsByDentist) { this.appointmentsByDentist = appointmentsByDentist; }

    public List<Map<String, Object>> getAppointmentsByTreatment() { return appointmentsByTreatment; }
    public void setAppointmentsByTreatment(List<Map<String, Object>> appointmentsByTreatment) { this.appointmentsByTreatment = appointmentsByTreatment; }
}
