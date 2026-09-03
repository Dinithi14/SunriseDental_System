package com.sunrisedental.dao;

import com.sunrisedental.model.Dentist;
import java.util.List;

public interface DentistDAO {
    Dentist findById(int id);
    List<Dentist> findAll();
    List<Dentist> findAllActive();
    boolean create(Dentist dentist);
    boolean update(Dentist dentist);
    boolean delete(int id);
}
