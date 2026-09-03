package com.sunrisedental.dao;

import com.sunrisedental.model.Patient;
import java.util.List;

public interface PatientDAO {
    Patient findById(int id);
    Patient findByPatientCode(String code);
    List<Patient> findAll();
    List<Patient> search(String query);
    int create(Patient patient);
    boolean update(Patient patient);
    boolean delete(int id);
    String generateNextPatientCode();
}
