package com.example.room.di;


import com.example.room.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        RetrofitModule.class,
        ConnectivityManagerModule.class
})
@Singleton
public interface AppComponent {

    void inject(MainActivity activity);

}
