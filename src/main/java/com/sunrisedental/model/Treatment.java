package com.sunrisedental.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Treatment {
    private int id;
    private String treatmentCode;
    private String treatmentName;
    private String description;
    private BigDecimal standardCost;
    private int estimatedMinutes;
    private boolean active;
    private Timestamp createdAt;

    public Treatment() {}

    public Treatment(int id, String treatmentCode, String treatmentName, String description,
                     BigDecimal standardCost, int estimatedMinutes, boolean active, Timestamp createdAt) {
        this.id = id;
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.description = description;
        this.standardCost = standardCost;
        this.estimatedMinutes = estimatedMinutes;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTreatmentCode() { return treatmentCode; }
    public void setTreatmentCode(String treatmentCode) { this.treatmentCode = treatmentCode; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getStandardCost() { return standardCost; }
    public void setStandardCost(BigDecimal standardCost) { this.standardCost = standardCost; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
