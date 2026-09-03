package com.sunrisedental.model;

import java.sql.Timestamp;

public class AuditLog {
    private int id;
    private String actionType;
    private String tableName;
    private Integer recordId;
    private String performedBy;
    private String description;
    private Timestamp logTimestamp;

    public AuditLog() {}

    public AuditLog(int id, String actionType, String tableName, Integer recordId, String performedBy, String description, Timestamp logTimestamp) {
        this.id = id;
        this.actionType = actionType;
        this.tableName = tableName;
        this.recordId = recordId;
        this.performedBy = performedBy;
        this.description = description;
        this.logTimestamp = logTimestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getLogTimestamp() { return logTimestamp; }
    public void setLogTimestamp(Timestamp logTimestamp) { this.logTimestamp = logTimestamp; }
}
