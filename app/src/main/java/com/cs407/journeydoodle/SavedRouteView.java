package com.cs407.journeydoodle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavedRouteView extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Button deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Journey Doodle");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_route);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String id = (String) Installation.id(mapFragment.getContext());
        Log.i("Info", "Printing user id: " + id);
    }


    public void onMapReady(GoogleMap googleMap) {

    }
}
