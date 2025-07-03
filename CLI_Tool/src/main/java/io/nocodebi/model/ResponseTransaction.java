package io.nocodebi.model;

import java.util.UUID;

public class ResponseTransaction {

    private String transactionID;

    private long timestamp;

    private double executionTime; // Execution time in seconds (with fractions)

    public ResponseTransaction() {

        this.transactionID = UUID.randomUUID().toString(); // Generate unique transaction ID

        this.timestamp = System.currentTimeMillis(); // Set the current timestamp

        this.executionTime = 0.0; // Default execution time is 0.0

    }

    public void calculateExecution() {

        this.executionTime = (System.currentTimeMillis() - this.timestamp) / 1000.0;

    }

    // Getters and Setters

    public String getTransactionID() {

        return transactionID;

    }

    public void setTransactionID(String transactionID) {

        this.transactionID = transactionID;

    }

    public long getTimestamp() {

        return timestamp;

    }

    public void setTimestamp(long timestamp) {

        this.timestamp = timestamp;

    }

    public double getExecutionTime() {

        return executionTime;

    }

    public void setExecutionTime(double executionTime) {

        this.executionTime = executionTime;

    }

}

