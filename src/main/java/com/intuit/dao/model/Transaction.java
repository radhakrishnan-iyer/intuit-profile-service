package com.intuit.dao.model;

import com.google.gson.Gson;

public class Transaction {

    private String correlationId;
    private String customerId;
    private int versionNo;
    private TransactionStatus transactionStatus;
    private String inputRequestMessage;
    private String profileId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getInputRequestMessage() {
        return inputRequestMessage;
    }

    public void setInputRequestMessage(String inputRequestMessage) {
        this.inputRequestMessage = inputRequestMessage;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
