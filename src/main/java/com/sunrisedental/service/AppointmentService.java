package com.sunrisedental.service;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.DAOFactory;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dto.AppointmentDetailDTO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Patient;
import com.sunrisedental.service.observer.NotificationPublisher;
import com.sunrisedental.util.ValidationUtil;

import java.sql.Date;
import java.util.List;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final PatientDAO patientDAO;
    private final NotificationPublisher notificationPublisher;

    public AppointmentService() {
        this.appointmentDAO = DAOFactory.getAppointmentDAO();
        this.patientDAO = DAOFactory.getPatientDAO();
        this.notificationPublisher = new NotificationPublisher();
    }

    public AppointmentService(AppointmentDAO appointmentDAO, PatientDAO patientDAO, NotificationPublisher notificationPublisher) {
        this.appointmentDAO = appointmentDAO;
        this.patientDAO = patientDAO;
        this.notificationPublisher = notificationPublisher != null ? notificationPublisher : new NotificationPublisher();
    }

    public AppointmentDetailDTO getAppointmentByNumber(String appointmentNumber) {
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            return null;
        }
        return appointmentDAO.findDetailByNumber(appointmentNumber.trim());
    }

    public AppointmentDetailDTO getAppointmentById(int id) {
        return appointmentDAO.findDetailById(id);
    }

    public List<AppointmentDetailDTO> getAllAppointments() {
        return appointmentDAO.findAllDetails();
    }

    public List<AppointmentDetailDTO> searchAppointments(String query, String status, Date fromDate, Date toDate, Integer dentistId) {
        return appointmentDAO.searchAppointments(query, status, fromDate, toDate, dentistId);
    }

    /**
     * Books a new appointment with strict double-booking conflict prevention.
     */
    public AppointmentDetailDTO bookAppointment(Appointment appointment, Patient newPatientIfAny) throws Exception {
        // 1. Resolve Patient ID
        int patientId = appointment.getPatientId();
        if (patientId <= 0 && newPatientIfAny != null) {
            if (!ValidationUtil.isNotEmpty(newPatientIfAny.getFullName())) {
                throw new IllegalArgumentException("Patient Name is required.");
            }
            if (!ValidationUtil.isValidPhoneNumber(newPatientIfAny.getContactNumber())) {
                throw new IllegalArgumentException("Valid 10-digit Sri Lankan phone number is required.");
            }
            patientId = patientDAO.create(newPatientIfAny);
            if (patientId <= 0) {
                throw new IllegalStateException("Failed to register patient for appointment.");
            }
            appointment.setPatientId(patientId);
        } else if (patientId <= 0) {
            throw new IllegalArgumentException("Please select or register a valid patient.");
        }

        // 2. Validate Appointment Fields
        if (appointment.getDentistId() <= 0) {
            throw new IllegalArgumentException("Please select an attending dentist.");
        }
        if (appointment.getTreatmentId() <= 0) {
            throw new IllegalArgumentException("Please select a treatment type.");
        }
        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Appointment date is required.");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment time is required.");
        }

        // 3. Double Booking Conflict Detection
        boolean isSlotTaken = appointmentDAO.checkDentistSlotConflict(
                appointment.getDentistId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                null
        );

        if (isSlotTaken) {
            throw new IllegalStateException(String.format(
                    "Conflict detected: The selected Dentist already has an active appointment on %s at %s. Please select a different time slot.",
                    appointment.getAppointmentDate(), appointment.getAppointmentTime()));
        }

        // 4. Save Appointment
        int createdId = appointmentDAO.create(appointment);
        if (createdId <= 0) {
            throw new RuntimeException("Failed to persist appointment in database.");
        }

        // 5. Retrieve full detail DTO
        AppointmentDetailDTO detailDTO = appointmentDAO.findDetailById(createdId);

        // 6. Notify Observers (SMS & Email simulation)
        if (detailDTO != null) {
            notificationPublisher.notifyAppointmentScheduled(detailDTO);
        }

        return detailDTO;
    }

    /**
     * Updates an existing appointment with conflict checking.
     */
    public boolean updateAppointment(Appointment appointment) throws Exception {
        boolean isSlotTaken = appointmentDAO.checkDentistSlotConflict(
                appointment.getDentistId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getId()
        );

        if (isSlotTaken) {
            throw new IllegalStateException("Dentist is already booked for this updated date and time slot.");
        }

        return appointmentDAO.update(appointment);
    }

    /**
     * Updates appointment status (e.g. COMPLETED, CANCELLED).
     */
    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        AppointmentDetailDTO before = appointmentDAO.findDetailById(appointmentId);
        boolean ok = appointmentDAO.updateStatus(appointmentId, newStatus);
        if (ok && before != null) {
            notificationPublisher.notifyAppointmentStatusChanged(before, before.getStatus(), newStatus);
        }
        return ok;
    }

    public boolean deleteAppointment(int appointmentId) {
        return appointmentDAO.delete(appointmentId);
    }
}
