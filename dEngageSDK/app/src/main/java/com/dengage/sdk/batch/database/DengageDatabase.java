package com.dengage.sdk.batch.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dengage.sdk.batch.database.dao.ModelDao;

@Database(entities = {ModelEntity.class}, version = 1)
public abstract class DengageDatabase extends RoomDatabase {
    public abstract ModelDao modelDao();
}
