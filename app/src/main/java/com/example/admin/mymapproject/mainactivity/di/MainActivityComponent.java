package com.example.admin.mymapproject.mainactivity.di;

import com.example.admin.mymapproject.mainactivity.MainActivity;

import dagger.Component;

/**
 * Created by Admin on 10/24/2017.
 */

@Component(modules = MainActivityModule.class)
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);
}
