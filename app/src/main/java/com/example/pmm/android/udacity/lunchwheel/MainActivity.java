package com.example.pmm.android.udacity.lunchwheel;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pmm.android.udacity.lunchwheel.data.DataContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean mIsTwoPanel = false;

    SharedPreferences prefs;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastDeviceLocation;

    private ListView mListView;
    private WheelAdapter mWheelAdapter;

    public static final int RESTAURANT_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        * Set the location mode to device if it hasn't been set yet.
        */
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!prefs.contains(Constants.PREF_LOCATION_MODE)) {
            prefs.edit()
                    .putString(Constants.PREF_LOCATION_MODE, Constants.PREF_LOCATION_MODE_DEVICE)
                    .commit();
        }

        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (findViewById(R.id.set_location_fragment) != null) {
            mIsTwoPanel = true;
            Log.i(TAG, "Using Two Panel Mode");
        } else {
            Log.i(TAG, "Using Single Panel Mode.");
        }

        // Create a new Adapter and bind it to the ListView
        mWheelAdapter = new WheelAdapter(this, null, 0);
        mListView = (ListView) findViewById(R.id.restaurants_listView);
        mListView.setAdapter(mWheelAdapter);
        getLoaderManager().initLoader(RESTAURANT_LOADER_ID, null, this);

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

    @Override
    public void onStart() {
        Log.i(TAG, "onStart() called");

        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop() called");

        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume() called");

        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected() called");

        mLastDeviceLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended() called");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed() called. connectionResult="
                + connectionResult.toString());

    }

    private void updateLocation() {

        String locationMode = prefs.getString(Constants.PREF_LOCATION_MODE, "");

        if (Constants.PREF_LOCATION_MODE_DEVICE.equals(locationMode)) {

            prefs.edit()
                    .putFloat(Constants.PREF_DEVICE_LATITUDE, (float) mLastDeviceLocation.getLatitude())
                    .putFloat(Constants.PREF_DEVICE_LONGITUDE, (float) mLastDeviceLocation.getLongitude())
                    .commit();

            if (Constants.PREF_LOCATION_MODE_DEVICE.equals(
                    prefs.getString(Constants.PREF_LOCATION_MODE, ""))) {

                SearchService.updateSearchResults(this);
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                DataContract.RestaurantEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mWheelAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWheelAdapter.swapCursor(null);
    }
}
