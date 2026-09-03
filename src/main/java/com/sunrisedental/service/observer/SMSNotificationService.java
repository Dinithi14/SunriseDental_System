package com.sunrisedental.service.observer;

import com.sunrisedental.config.DatabaseConnection;
import com.sunrisedental.dto.AppointmentDetailDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

/**
 * Concrete Observer: SMSNotificationService
 * Simulates and logs automated SMS alerts for patient appointment bookings and reminders.
 */
public class SMSNotificationService implements NotificationObserver {

    private static final Logger LOGGER = Logger.getLogger(SMSNotificationService.class.getName());

    @Override
    public void onAppointmentScheduled(AppointmentDetailDTO appointment) {
        String msg = String.format("Sunrise Dental Clinic: Dear %s, your appointment %s is confirmed with %s on %s at %s. Location: Colombo.",
                appointment.getPatientName(), appointment.getAppointmentNumber(), appointment.getDentistName(),
                appointment.getAppointmentDate(), appointment.getAppointmentTime());
        
        sendSMS(appointment.getAppointmentId(), appointment.getPatientContact(), msg);
    }

    @Override
    public void onAppointmentStatusChanged(AppointmentDetailDTO appointment, String oldStatus, String newStatus) {
        String msg = String.format("Sunrise Dental Clinic: Dear %s, your appointment %s status has been updated to %s.",
                appointment.getPatientName(), appointment.getAppointmentNumber(), newStatus);
        
        sendSMS(appointment.getAppointmentId(), appointment.getPatientContact(), msg);
    }

    private void sendSMS(int appointmentId, String contactNumber, String message) {
        LOGGER.info(String.format("[SMS SIMULATOR] Sending SMS to %s: \"%s\"", contactNumber, message));
        
        // Persist notification log into database if connection is active
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO sms_email_notifications (appointment_id, recipient_contact, recipient_email, notification_type, message, status) VALUES (?, ?, ?, 'SMS', ?, 'SENT')")) {
            ps.setInt(1, appointmentId);
            ps.setString(2, contactNumber != null ? contactNumber : "N/A");
            ps.setString(3, null);
            ps.setString(4, message);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.fine("SMS log skipped (offline mode / table not present): " + e.getMessage());
        }
    }
}
