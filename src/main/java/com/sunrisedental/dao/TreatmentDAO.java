package com.sunrisedental.dao;

import com.sunrisedental.model.Treatment;
import java.util.List;

public interface TreatmentDAO {
    Treatment findById(int id);
    List<Treatment> findAll();
    List<Treatment> findAllActive();
    boolean create(Treatment treatment);
    boolean update(Treatment treatment);
    boolean delete(int id);
}
