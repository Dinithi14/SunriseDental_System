package com.sunrisedental;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dto.AppointmentDetailDTO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Patient;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.observer.NotificationPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for Appointment Service & Double Booking Prevention.
 */
public class AppointmentServiceTest {

    private AppointmentService appointmentService;
    private MockAppointmentDAO mockApptDAO;
    private MockPatientDAO mockPatientDAO;

    @BeforeEach
    public void setUp() {
        mockApptDAO = new MockAppointmentDAO();
        mockPatientDAO = new MockPatientDAO();
        appointmentService = new AppointmentService(mockApptDAO, mockPatientDAO, new NotificationPublisher());
    }

    @Test
    @DisplayName("Test Successful Appointment Booking")
    public void testSuccessfulBooking() throws Exception {
        Appointment appt = new Appointment();
        appt.setPatientId(1);
        appt.setDentistId(1);
        appt.setTreatmentId(2);
        appt.setAppointmentDate(Date.valueOf("2026-09-10"));
        appt.setAppointmentTime(Time.valueOf("09:30:00"));

        AppointmentDetailDTO booked = appointmentService.bookAppointment(appt, null);
        assertNotNull(booked);
        assertEquals("APT-2026-0001", booked.getAppointmentNumber());
        assertEquals(1, mockApptDAO.appointments.size());
    }

    @Test
    @DisplayName("Test Double Booking Prevention (Conflict Avoidance)")
    public void testDoubleBookingConflict() {
        // Book initial appointment
        Appointment appt1 = new Appointment();
        appt1.setPatientId(1);
        appt1.setDentistId(2);
        appt1.setTreatmentId(1);
        appt1.setAppointmentDate(Date.valueOf("2026-09-10"));
        appt1.setAppointmentTime(Time.valueOf("10:00:00"));

        assertDoesNotThrow(() -> appointmentService.bookAppointment(appt1, null));

        // Attempt second booking with same dentist on same date and time
        Appointment appt2 = new Appointment();
        appt2.setPatientId(2);
        appt2.setDentistId(2); // Same Dentist
        appt2.setTreatmentId(3);
        appt2.setAppointmentDate(Date.valueOf("2026-09-10")); // Same Date
        appt2.setAppointmentTime(Time.valueOf("10:00:00")); // Same Time

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            appointmentService.bookAppointment(appt2, null);
        });

        assertTrue(ex.getMessage().contains("Conflict detected"));
    }

    // ==========================================
    // Mock DAO Implementations for Pure Testing
    // ==========================================
    private static class MockAppointmentDAO implements AppointmentDAO {
        List<Appointment> appointments = new ArrayList<>();

        @Override
        public Appointment findById(int id) { return null; }
        @Override
        public Appointment findByAppointmentNumber(String num) { return null; }
        @Override
        public AppointmentDetailDTO findDetailByNumber(String num) { return null; }

        @Override
        public AppointmentDetailDTO findDetailById(int id) {
            AppointmentDetailDTO dto = new AppointmentDetailDTO();
            dto.setAppointmentId(id);
            dto.setAppointmentNumber("APT-2026-0001");
            dto.setPatientName("Test Patient");
            dto.setDentistName("Dr. Perera");
            dto.setAppointmentDate(Date.valueOf("2026-09-10"));
            dto.setAppointmentTime(Time.valueOf("09:30:00"));
            return dto;
        }

        @Override
        public List<AppointmentDetailDTO> findAllDetails() { return new ArrayList<>(); }
        @Override
        public List<AppointmentDetailDTO> searchAppointments(String q, String s, Date f, Date t, Integer d) { return new ArrayList<>(); }

        @Override
        public boolean checkDentistSlotConflict(int dentistId, Date date, Time time, Integer excludeId) {
            return appointments.stream().anyMatch(a ->
                    a.getDentistId() == dentistId &&
                    a.getAppointmentDate().equals(date) &&
                    a.getAppointmentTime().equals(time));
        }

        @Override
        public int create(Appointment appointment) {
            appointment.setId(appointments.size() + 1);
            if (appointment.getAppointmentNumber() == null) {
                appointment.setAppointmentNumber("APT-2026-0001");
            }
            appointments.add(appointment);
            return appointment.getId();
        }

        @Override public boolean update(Appointment appointment) { return true; }
        @Override public boolean updateStatus(int appointmentId, String status) { return true; }
        @Override public boolean delete(int id) { return true; }
        @Override public String generateNextAppointmentNumber() { return "APT-2026-0001"; }
        @Override public int countTotal() { return appointments.size(); }
        @Override public int countByStatus(String status) { return 0; }
        @Override public int countToday() { return 0; }
    }

    private static class MockPatientDAO implements PatientDAO {
        @Override public Patient findById(int id) { return null; }
        @Override public Patient findByPatientCode(String code) { return null; }
        @Override public List<Patient> findAll() { return new ArrayList<>(); }
        @Override public List<Patient> search(String query) { return new ArrayList<>(); }
        @Override public int create(Patient patient) { return 1; }
        @Override public boolean update(Patient patient) { return true; }
        @Override public boolean delete(int id) { return true; }
        @Override public String generateNextPatientCode() { return "PAT-001"; }
    }
}
