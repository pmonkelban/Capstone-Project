package com.example.pmm.android.udacity.lunchwheel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.fragments.SetLocationFragment;

public class SetLocationActivity extends AppCompatActivity {

    private static final String LOCATION_FRAGMENT_ID = "LOCATION_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_location_activity);

        if (savedInstanceState == null)  {

            SetLocationFragment fragment = new SetLocationFragment();

            getFragmentManager().beginTransaction()
                    .replace(R.id.set_location_frame, fragment, LOCATION_FRAGMENT_ID)
                    .commit();
        }
    }
}
