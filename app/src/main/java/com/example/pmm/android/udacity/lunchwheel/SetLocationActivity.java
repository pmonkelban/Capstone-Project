package com.example.pmm.android.udacity.lunchwheel;

import android.app.Activity;
import android.os.Bundle;

public class SetLocationActivity extends Activity {

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
