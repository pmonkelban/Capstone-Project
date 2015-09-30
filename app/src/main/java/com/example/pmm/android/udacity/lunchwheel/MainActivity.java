package com.example.pmm.android.udacity.lunchwheel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean mIsTwoPanel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.set_location_fragment) != null)  {
            mIsTwoPanel = true;
            Log.i(TAG, "Using Two Panel Mode");
        } else  {
            Log.i(TAG, "Using Single Panel Mode.");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!mIsTwoPanel) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                break;

            case R.id.action_location:
                startActivity(new Intent(this, SetLocationActivity.class));
                break;

            default:
                // Do nothing.

        }

        return super.onOptionsItemSelected(item);
    }


}
