package com.example.room;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Model.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract UserDAO userDAO();

}
