package com.example.room.di;

import android.content.Context;
import android.net.ConnectivityManager;

import dagger.Module;
import dagger.Provides;

@Module
public class ConnectivityManagerModule {
    private Context context;

    public ConnectivityManagerModule(Context context) {
        this.context = context;
    }

    @Provides
    ConnectivityManager NetworkInfo() {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
