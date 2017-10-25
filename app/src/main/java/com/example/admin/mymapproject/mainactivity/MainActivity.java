package com.example.admin.mymapproject.mainactivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.mymapproject.MapsActivity;
import com.example.admin.mymapproject.R;
import com.example.admin.mymapproject.RetrofitHelper;
import com.example.admin.mymapproject.mainactivity.di.DaggerMainActivityComponent;
import com.example.admin.mymapproject.mainactivity.di.MainActivityModule;
import com.example.admin.mymapproject.model.AddressComponent;
import com.example.admin.mymapproject.model.Result;
import com.example.admin.mymapproject.model.Results;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.admin.mymapproject.R.id.btnGetApiLocation;
import static com.example.admin.mymapproject.R.id.btnGetLocation;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {

    private static final String TAG = "mainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 10;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    boolean permission = false;
    EditText etLocation;

    @Inject
    MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLocation = findViewById(R.id.etLocation);

        DaggerMainActivityComponent.builder()
                .mainActivityModule(new MainActivityModule())
                .build()
                .inject(this);

        presenter.addView(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermission();
    }

    @Override
    public void showError() {

    }

    @Override
    public void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "updateLocation: add permission");
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                Log.d(TAG, "onSuccess: " + location);
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

    }

    @Override
    public void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Does not have permission");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRequestRationals();

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                requestContactsPermission();
                // MY_PERMISSIONS_REQUEST_READ_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            //presenter.getLocation();
            permission = true;
            Log.d(TAG, "onCreate: Permisson is already granted");

        }
    }

    private void showRequestRationals() {
        Log.d(TAG, "onCreate: Should show rationale");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Explination")
                .setMessage("Please allow this Permission to read contacts")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestContactsPermission();
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Tink Again", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void requestContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_LOCATION);
        Log.d(TAG, "onCreate: Requesting permission");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //presenter.getLocation();
                    permission = true;
                    Log.d(TAG, "onRequestPermissionsResult: Permisson granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void goToMap(View view) {
        String location = etLocation.getText().toString();
        final Intent intent = new Intent(MainActivity.this, MapsActivity.class);

        if(!permission){
            Toast.makeText(this, "Please add permission", Toast.LENGTH_SHORT).show();
        } else {
            switch (view.getId()){
                case btnGetLocation:

                    intent.putExtra("location", location);
                    startActivity(intent);

                    break;
                case btnGetApiLocation:
                    Map<String, String> query = new ArrayMap<>();
                    final List<String> address = new ArrayList<>();
                    final List<Double> lat = new ArrayList<>(), lng = new ArrayList<>();

                    if(location.contains(",")){
                        query.put("latlng", location);
                        query.put("key", "AIzaSyBQWPyn1jVKbGQldmTm9owmqlmuGB8BSNI");

                        RetrofitHelper.getCall(query)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Observer<Results>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                        Log.d(TAG, "onSubscribe: " + d.toString());
                                    }

                                    @Override
                                    public void onNext(@io.reactivex.annotations.NonNull Results results) {
                                        lat.add(results.getResults().get(0).getGeometry().getLocation().getLat());
                                        lng.add(results.getResults().get(0).getGeometry().getLocation().getLng());
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        Log.d(TAG, "onError: " + e.toString());
                                    }

                                    @Override
                                    public void onComplete() {

                                        intent.putExtra("location", lng.get(0).toString() + "," + lat.get(0).toString());
                                        startActivity(intent);

                                        Log.d(TAG, "onComplete: ");
                                    }
                                });
                    }else{
                        query.put("address", location);
                        query.put("key", "AIzaSyBQWPyn1jVKbGQldmTm9owmqlmuGB8BSNI");

                        RetrofitHelper.getCall(query)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Observer<Results>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                        Log.d(TAG, "onSubscribe: " + d.toString());
                                    }

                                    @Override
                                    public void onNext(@io.reactivex.annotations.NonNull Results results) {
                                        address.add(results.getResults().get(0).getAddressComponents().get(0).getLongName());
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        Log.d(TAG, "onError: " + e.toString());
                                    }

                                    @Override
                                    public void onComplete() {

                                        intent.putExtra("location", address.get(0));
                                        startActivity(intent);

                                        Log.d(TAG, "onComplete: ");
                                    }
                                });
                    }
                    break;
            }
        }
    }
}
