package com.sunrisedental.service;

import com.sunrisedental.dao.DAOFactory;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.model.Dentist;

import java.util.List;

public class DentistService {

    private final DentistDAO dentistDAO;

    public DentistService() {
        this.dentistDAO = DAOFactory.getDentistDAO();
    }

    public DentistService(DentistDAO dentistDAO) {
        this.dentistDAO = dentistDAO;
    }

    public Dentist getDentistById(int id) {
        return dentistDAO.findById(id);
    }

    public List<Dentist> getAllDentists() {
        return dentistDAO.findAll();
    }

    public List<Dentist> getActiveDentists() {
        return dentistDAO.findAllActive();
    }

    public boolean saveDentist(Dentist dentist) {
        if (dentist.getId() > 0) {
            return dentistDAO.update(dentist);
        } else {
            return dentistDAO.create(dentist);
        }
    }
}
