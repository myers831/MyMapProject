package com.example.admin.mymapproject;

import android.util.Log;

import com.example.admin.mymapproject.model.Results;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Admin on 10/14/2017.
 */

public class RetrofitHelper {

    public static final String BASE_URL = "https://maps.googleapis.com";

    static public Retrofit create(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }

    static public Observable<Results> getCall(Map<String, String> query){
        Retrofit retrofit = create();
        RequestService service = retrofit.create(RequestService.class);
        Log.d(TAG, "getCall: " + query);
        return service.responseService(query);
    }

    public interface RequestService{
        //@GET("{query}")
        //Observable<Search> responseService();
        //Observable<Search> responseService(@Path(value = "query", encoded=true) final String query);

        @GET("/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyBQWPyn1jVKbGQldmTm9owmqlmuGB8BSNI")
        Observable<Results> responseService(@QueryMap Map<String, String> query);
    }
}
