package com.sunrisedental.service;

import com.sunrisedental.dao.DAOFactory;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Treatment;

import java.util.List;

public class TreatmentService {

    private final TreatmentDAO treatmentDAO;

    public TreatmentService() {
        this.treatmentDAO = DAOFactory.getTreatmentDAO();
    }

    public TreatmentService(TreatmentDAO treatmentDAO) {
        this.treatmentDAO = treatmentDAO;
    }

    public Treatment getTreatmentById(int id) {
        return treatmentDAO.findById(id);
    }

    public List<Treatment> getAllTreatments() {
        return treatmentDAO.findAll();
    }

    public List<Treatment> getActiveTreatments() {
        return treatmentDAO.findAllActive();
    }

    public boolean saveTreatment(Treatment treatment) {
        if (treatment.getId() > 0) {
            return treatmentDAO.update(treatment);
        } else {
            return treatmentDAO.create(treatment);
        }
    }
}
