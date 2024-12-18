package com.waka.techoadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener, SensorEventListener {   //implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar materialToolbar;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new HomeFragment());

        // Sensor ----------------------------------------------------------------------------------
        requestPermissions(new String[]{ Manifest.permission.ACTIVITY_RECOGNITION}, 100);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        if (sensor != null) {
            sensorManager.registerListener(MainActivity.this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        // Sensor ----------------------------------------------------------------------------------

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.menuViewer);
        materialToolbar = findViewById(R.id.materialToolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        setSupportActionBar(materialToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.imageMenuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        if (item.getItemId() == R.id.bottomNavHome || item.getItemId() == R.id.sideNavHome) {
            loadFragment(new HomeFragment());

            findViewById(R.id.bottomNavHome).setSelected(true);
            findViewById(R.id.sideNavHome).setSelected(true);

        } else if (item.getItemId() == R.id.bottomNavProducts) {
            loadFragment(new AddProductFragment());

        } else if (item.getItemId()==R.id.bottomNavProfile) {
            loadFragment(new ProfileFragment());

        } else if (item.getItemId()==R.id.bottomNavLogout) {
            firebaseAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.bottomNavOrders) {
            loadFragment(new OrderFragment());

        }
//        else if (item.getItemId() == R.id.bottomNavWishlist) {
//            loadFragment(new WishlistFragment());
//
//        } else if (item.getItemId() == R.id.bottomNavLogin) {
//            loadFragment(new LoginFragment());
//
//        } else if (item.getItemId() == R.id.bottomNavProducts) {
//            loadFragment(new AllProductsFragment());
//
//        } else if (item.getItemId() == R.id.bottomNavNotifi) {
//            loadFragment(new NotificationFragment());
////            findViewById(R.id.bottomNavNotifi).setSelected(true);
//
//        } else if (item.getItemId() == R.id.bottomNavAbout) {
//            loadFragment(new AboutFragment());
//
//        }else if (item.getItemId() == R.id.bottomNavOrders) {
//            loadFragment(new OrderFragment());
//
//        }

        drawerLayout.close();
        return true;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_LIGHT:
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.screenBrightness = event.values[0] / 255.0f;
                getWindow().setAttributes(layoutParams);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
