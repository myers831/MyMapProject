package com.example.admin.mymapproject.mainactivity;


import com.example.admin.mymapproject.BasePresenter;
import com.example.admin.mymapproject.BaseView;

/**
 * Created by Admin on 10/24/2017.
 */

public interface MainActivityContract {
    interface View extends BaseView {
        public void updateLocation();
        public void checkPermission();
    }

    interface Presenter extends BasePresenter<View> {
        public void getLocation();
    }
}
