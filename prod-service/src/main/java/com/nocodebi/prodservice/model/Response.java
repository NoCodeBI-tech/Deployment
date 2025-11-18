package com.nocodebi.prodservice.model;

import java.util.Collections;

public class Response {

    private Object data;

    private String error;

    private String success;

    private ResponseTransaction transaction;

    // Default constructor with initialization

    public Response() {

    }

    public Response(String error, Object data, String success, ResponseTransaction transaction) {

        this.error = error != null ? error : "";

        this.data = data != null ? data : Collections.emptyMap();

        this.success = success != null ? success : "";

        this.transaction = transaction != null ? transaction : new ResponseTransaction();

        this.transaction.end();

    }

    // Parameterized constructor

    public void setDefault() {

        this.data = null;

        this.error = "";

        this.success = "";

        this.transaction = new ResponseTransaction(); // Initialize with a default transaction

    }

    // Getters and Setters

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public ResponseTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(ResponseTransaction transaction) {
        this.transaction = transaction;
    }
}

