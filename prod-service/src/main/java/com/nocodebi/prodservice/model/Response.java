package com.nocodebi.prodservice.model;

public class Response {

    private Object data;

    private String status;

    private String message; // InputField for success or error messages

    private ResponseTransaction transaction;

    public Response() {

    }

    public Response(String status, Object data, String message, ResponseTransaction transaction) {

        this.status = status != null ? status : "UNKNOWN";

        this.data = data;

        this.message = message != null ? message : "";

        this.transaction = transaction != null ? transaction : new ResponseTransaction();

        this.transaction.calculateExecution();

    }

    public Object getData() {

        return data;

    }

    public void setData(Object data) {

        this.data = data;

    }

    public String getStatus() {

        return status;

    }

    public void setStatus(String status) {

        this.status = status;

    }

    public String getMessage() {

        return message;

    }

    public void setMessage(String message) {

        this.message = message;

    }

    public ResponseTransaction getTransaction() {

        return transaction;

    }

    public void setTransaction(ResponseTransaction transaction) {

        this.transaction = transaction;

    }

}