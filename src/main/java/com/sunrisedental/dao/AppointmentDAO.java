package com.sunrisedental.dao;

import com.sunrisedental.dto.AppointmentDetailDTO;
import com.sunrisedental.model.Appointment;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface AppointmentDAO {
    Appointment findById(int id);
    Appointment findByAppointmentNumber(String appointmentNumber);
    AppointmentDetailDTO findDetailByNumber(String appointmentNumber);
    AppointmentDetailDTO findDetailById(int id);
    List<AppointmentDetailDTO> findAllDetails();
    List<AppointmentDetailDTO> searchAppointments(String query, String status, Date fromDate, Date toDate, Integer dentistId);
    boolean checkDentistSlotConflict(int dentistId, Date date, Time time, Integer excludeAppointmentId);
    int create(Appointment appointment);
    boolean update(Appointment appointment);
    boolean updateStatus(int appointmentId, String status);
    boolean delete(int id);
    String generateNextAppointmentNumber();
    int countTotal();
    int countByStatus(String status);
    int countToday();
}
