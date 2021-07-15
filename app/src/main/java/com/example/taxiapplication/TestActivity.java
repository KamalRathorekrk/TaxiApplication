package com.example.taxiapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

public class TestActivity extends AppCompatActivity{

    DrawerLayout mDrawerLayout;
    NavigationView navView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        mapView = (MapView) findViewById(R.id.mapView2);
//        mapView.onCreate(savedInstanceState);



        mDrawerLayout = findViewById(R.id.drawer);
          toolbar = findViewById(R.id.toolbar_main);

        setupDrawer();

    }
    private void setupDrawer() {
        // Show the burger button on the ActionBar
        System.out.print("Profile i s clicked1111");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(TestActivity.this,
                mDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);

        toggle.syncState();
//        NavigationView navigationView = (NavigationView) findViewById(R.id.navView1);
//        navigationView.setNavigationItemSelectedListener(this);
    }


    public void onNavigationButtonClick1(View view) {

//        TextView tvMain = findViewById(R.id.tv_main);
//        tvMain.setText(((Button) view).getText().toString());

        System.out.print("Profile i s clicked22356");
        switch (view.getId()) {
            case R.id.profile:
                // Do something with button 1
                startActivity(new Intent(this,HomeScreen.class));
                System.out.print("Profile i s clicked");
                Toast.makeText(this, "Hello Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnLogout:
                System.out.print("Profile i s clicked");
                Toast.makeText(this, "Hello Profile 22", Toast.LENGTH_SHORT).show();
                break;

            case R.id.rideshistory:

                System.out.print("Profile i s clicked");
                Toast.makeText(this, "Hello Profile 33", Toast.LENGTH_SHORT).show();
                break;

        }
    }



}