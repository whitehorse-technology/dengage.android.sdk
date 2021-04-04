package com.dengage.sdk.models;

import com.google.gson.annotations.SerializedName;

public class TransactionalOpen extends AbstractOpen {
    @SerializedName("transactionId")
    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
