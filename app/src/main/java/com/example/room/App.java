package com.example.room;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {
    static public App instance;
    private Database database;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        database = Room.databaseBuilder(this, Database.class, "database")
                .fallbackToDestructiveMigration()
                .build();
    }

    public Database getDatabase() {
        return database;
    }
}

