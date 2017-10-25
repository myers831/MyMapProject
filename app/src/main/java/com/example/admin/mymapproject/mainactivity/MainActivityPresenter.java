package com.example.admin.mymapproject.mainactivity;

/**
 * Created by Admin on 10/24/2017.
 */

public class MainActivityPresenter implements MainActivityContract.Presenter {

    MainActivityContract.View view;

    @Override
    public void addView(MainActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void getLocation() {
        view.updateLocation();
    }
}
