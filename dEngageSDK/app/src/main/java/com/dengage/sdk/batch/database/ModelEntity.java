package com.dengage.sdk.batch.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

@Entity
public class ModelEntity {
    @PrimaryKey(autoGenerate = true)
    public long uid;

    @ColumnInfo
    public String inegrationKey;

    @ColumnInfo
    public long transactionId;

    @ColumnInfo
    public String json;

    @ColumnInfo
    public String type;

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
