package com.sunrisedental.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Patient {
    private int id;
    private String patientCode;
    private String fullName;
    private String nicPassport;
    private String contactNumber;
    private String email;
    private String address;
    private Date dateOfBirth;
    private String gender; // MALE, FEMALE, OTHER
    private String bloodGroup;
    private String emergencyContact;
    private String medicalHistory;
    private Timestamp createdAt;

    public Patient() {}

    public Patient(int id, String patientCode, String fullName, String nicPassport, String contactNumber,
                   String email, String address, Date dateOfBirth, String gender, String bloodGroup,
                   String emergencyContact, String medicalHistory, Timestamp createdAt) {
        this.id = id;
        this.patientCode = patientCode;
        this.fullName = fullName;
        this.nicPassport = nicPassport;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
        this.medicalHistory = medicalHistory;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPatientCode() { return patientCode; }
    public void setPatientCode(String patientCode) { this.patientCode = patientCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNicPassport() { return nicPassport; }
    public void setNicPassport(String nicPassport) { this.nicPassport = nicPassport; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
