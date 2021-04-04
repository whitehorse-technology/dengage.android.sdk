package com.dengage.sdk.batch.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.dengage.sdk.batch.database.ModelEntity;

import java.util.List;

@Dao
public interface ModelDao {
    @Query("SELECT * from modelentity")
    List<ModelEntity> getAll();

    @Query("SELECT * from modelentity where transactionId = :transactionId")
    List<ModelEntity> getAll(long transactionId);

    @Insert
    void insertAll(ModelEntity... modelEntities);

    @Delete
    void delete(ModelEntity modelEntity);

    @Query("SELECT * from modelentity where transactionId != :transactionId")
    List<ModelEntity> getInvalids(long transactionId);

    @Query("DELETE from modelentity where transactionId == :transactionId")
    void deleteByTransactionId(long transactionId);

}
