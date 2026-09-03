package com.sunrisedental.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Dentist {
    private int id;
    private String dentistCode;
    private String fullName;
    private String specialization;
    private String contactNumber;
    private String email;
    private BigDecimal consultationFee;
    private String availableDays;
    private String roomNumber;
    private boolean active;
    private Timestamp createdAt;

    public Dentist() {}

    public Dentist(int id, String dentistCode, String fullName, String specialization, String contactNumber,
                   String email, BigDecimal consultationFee, String availableDays, String roomNumber,
                   boolean active, Timestamp createdAt) {
        this.id = id;
        this.dentistCode = dentistCode;
        this.fullName = fullName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
        this.consultationFee = consultationFee;
        this.availableDays = availableDays;
        this.roomNumber = roomNumber;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDentistCode() { return dentistCode; }
    public void setDentistCode(String dentistCode) { this.dentistCode = dentistCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
