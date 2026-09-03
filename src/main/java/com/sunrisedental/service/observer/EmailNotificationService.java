package com.sunrisedental.service.observer;

import com.sunrisedental.config.DatabaseConnection;
import com.sunrisedental.dto.AppointmentDetailDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

/**
 * Concrete Observer: EmailNotificationService
 * Simulates automated email confirmations and calendar invites for dental appointments.
 */
public class EmailNotificationService implements NotificationObserver {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationService.class.getName());

    @Override
    public void onAppointmentScheduled(AppointmentDetailDTO appointment) {
        if (appointment.getPatientEmail() == null || appointment.getPatientEmail().trim().isEmpty()) {
            return;
        }

        String subject = "Appointment Confirmation: " + appointment.getAppointmentNumber() + " - Sunrise Dental Clinic";
        String body = String.format(
                "Dear %s,\n\nYour dental consultation has been successfully reserved.\n\n" +
                "Reference Number: %s\nDentist: %s (%s)\nTreatment: %s\nDate: %s\nTime: %s\nRoom: %s\n\n" +
                "Thank you for choosing Sunrise Dental Clinic Colombo.",
                appointment.getPatientName(), appointment.getAppointmentNumber(),
                appointment.getDentistName(), appointment.getDentistSpecialization(),
                appointment.getTreatmentName(), appointment.getAppointmentDate(),
                appointment.getAppointmentTime(), appointment.getRoomNumber());

        sendEmail(appointment.getAppointmentId(), appointment.getPatientContact(), appointment.getPatientEmail(), subject, body);
    }

    @Override
    public void onAppointmentStatusChanged(AppointmentDetailDTO appointment, String oldStatus, String newStatus) {
        if (appointment.getPatientEmail() == null || appointment.getPatientEmail().trim().isEmpty()) {
            return;
        }

        String subject = "Appointment Update: " + appointment.getAppointmentNumber() + " - Sunrise Dental Clinic";
        String body = String.format("Dear %s,\n\nYour appointment %s status has been changed to: %s.\nSunrise Dental Clinic Colombo.",
                appointment.getPatientName(), appointment.getAppointmentNumber(), newStatus);

        sendEmail(appointment.getAppointmentId(), appointment.getPatientContact(), appointment.getPatientEmail(), subject, body);
    }

    private void sendEmail(int appointmentId, String contactNumber, String email, String subject, String body) {
        LOGGER.info(String.format("[EMAIL SIMULATOR] Dispatching Email to %s [%s]: \"%s\"", email, subject, body));

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO sms_email_notifications (appointment_id, recipient_contact, recipient_email, notification_type, message, status) VALUES (?, ?, ?, 'EMAIL', ?, 'SENT')")) {
            ps.setInt(1, appointmentId);
            ps.setString(2, contactNumber != null ? contactNumber : "N/A");
            ps.setString(3, email);
            ps.setString(4, "[" + subject + "] " + body);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.fine("Email log skipped: " + e.getMessage());
        }
    }
}
