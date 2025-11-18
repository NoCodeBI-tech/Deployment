package com.nocodebi.prodservice.model;

import com.nocodebi.prodservice.constant.Constant;
import com.nocodebi.prodservice.utils.Utilities;

import java.sql.Timestamp;
import java.util.UUID;

public class ResponseTransaction {

    private String transactionID;

    private String initiatedBy; // user emailId

    private String startTime;

    private String endTime;

    private double executionTime; // Execution time in seconds (with fractions)

    private long startMillis;

    private long endMillis;

    public ResponseTransaction(String initiatedBy) {
        this.transactionID = UUID.randomUUID().toString();
        this.initiatedBy = Utilities.isNotNullOrBlank(initiatedBy) ? initiatedBy : Constant.ANONYMOUS_USER;
        this.startMillis = System.currentTimeMillis();
        this.startTime = new Timestamp(this.startMillis).toString();
        this.executionTime = 0.0;
    }

    public ResponseTransaction() {

        this.transactionID = UUID.randomUUID().toString();
        this.initiatedBy = "system";
        this.startMillis = System.currentTimeMillis();
        this.startTime = new Timestamp(this.startMillis).toString();
        this.executionTime = 0.0;

    }

    public void end() {
        this.endMillis = System.currentTimeMillis();
        this.endTime = new Timestamp(this.endMillis).toString();
        this.executionTime = (this.endMillis - this.startMillis) / 1000.0; // seconds with fraction
    }

    // Getters and Setters

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public void setEndMillis(long endMillis) {
        this.endMillis = endMillis;
    }

}

