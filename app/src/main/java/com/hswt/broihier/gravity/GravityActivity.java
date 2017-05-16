package com.hswt.broihier.gravity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class GravityActivity extends AppCompatActivity {

    private static Activity gravityActivity;
    private final String TAG = "GravityActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment);

        if (fragment == null) {
            fragment = new GravityActivityFragment();
            fm.beginTransaction().add(R.id.fragment, fragment)
                    .commit();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numberOfSatellites = UniverseDrawingView.getNumberOfSatellites();
                double mass = UniverseDrawingView.getStarMass();
                Snackbar.make(view, "There are " + numberOfSatellites + " satellites\n" +
                        "The star's mass is: "+mass, Snackbar.LENGTH_LONG)
                        .setAction("Action",
                                null
                        ).show();
            }


        });

        gravityActivity = this;
    }

    public static Activity getGravityActivity() {
        return gravityActivity;
    }
}
