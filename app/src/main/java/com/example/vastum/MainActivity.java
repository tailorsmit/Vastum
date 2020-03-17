package com.example.vastum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int getIntentKey;
    private TextView name, email, number, currentLocation;
    String str="";
    FirebaseAuth mAuth;
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    BottomNavigationView bottomNavigation;
    private FusedLocationProviderClient mFusedLocationClient;

    //recycler view
    private RecyclerView recyclerView;
    private ArrayList<TV_TVPart_demo> tvPartList;
    private TVitemAdapter tvitemAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //creating a GoogleSignInClient object;
    private LocationCallback mLocationCallback;
    private LocationRequest mlocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIntentKey = getIntent().getIntExtra("Flag",0);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        currentLocation = findViewById(R.id.currentLocation);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginAcitivity.class));
            finish();
        }


        location();


        bottomnavigation();
        CreateList();
        BuildRecyclerView();
    }

    private void CreateList() {
        tvPartList = new ArrayList<>();
        tvPartList.add(new TV_TVPart_demo("WHERE IT IS", R.drawable.logo));
        tvPartList.add(new TV_TVPart_demo("HERE IT IS", R.drawable.monitor));
        tvPartList.add(new TV_TVPart_demo("WHERE IT IS", R.drawable.logo));
        tvPartList.add(new TV_TVPart_demo("HERE IT IS", R.drawable.monitor));
        tvPartList.add(new TV_TVPart_demo("WHERE IT IS", R.drawable.logo));
        tvPartList.add(new TV_TVPart_demo("HERE IT IS", R.drawable.monitor));
    }

    private void BuildRecyclerView() {
        recyclerView = findViewById(R.id.RecyclerTV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tvitemAdapter = new TVitemAdapter(tvPartList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tvitemAdapter);
        tvitemAdapter.setOnItemClickListener(new TVitemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });

    }

    private void bottomnavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_sell:
                        startActivity((new Intent(MainActivity.this, HomeActivity.class)).putExtra("Flag",0));
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        return true;
                    case R.id.navigation_redeem:
                        startActivity((new Intent(MainActivity.this, RedeemActivity.class)).putExtra("Flag",0));
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        return true;
                    case R.id.navigation_profile:
                        startActivity((new Intent(MainActivity.this, ProfileActivity.class)).putExtra("Flag",0));
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        return true;

                }
                return false;
            }
        });
    }


    private void location() {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocatinPermission();
            }

        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (getApplicationContext() != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        System.out.println(latLng);

                        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            List<Address> myList = myLocation.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            Address address = myList.get(0);
                            address.getLocality();
                            currentLocation.setText(address.getLocality());
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println(e.toString());
                        }

                    }
                }
                mFusedLocationClient.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
            }
        };
    }


    private void checkLocatinPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission to access your location")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();

                    }
                    break;
                }
            }
        }
    }
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    protected void onResume() {
        super.onResume();
        {    startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mlocationRequest,mLocationCallback,Looper.myLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

//        if(getIntentKey==1){
//            bottomNavigation.setSelectedItemId(R.id.navigation_sell);
//        }
//        else if(getIntentKey==2){
//            bottomNavigation.setSelectedItemId(R.id.navigation_redeem);
//        }
//        else if(getIntentKey==3){
//            bottomNavigation.setSelectedItemId(R.id.navigation_profile);
//        }

    }
}

