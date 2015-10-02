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
        LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean mIsTwoPanel = false;

    private TextView mLatTextView;
    private TextView mLonTextView;

    SharedPreferences prefs;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastDeviceLocation;

    private ListView mListView;
    private WheelAdapter mWheelAdapter;

    public static final int RESTAURANT_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (findViewById(R.id.set_location_fragment) != null)  {
            mIsTwoPanel = true;
            Log.i(TAG, "Using Two Panel Mode");
        } else  {
            Log.i(TAG, "Using Single Panel Mode.");
        }

        mLatTextView = (TextView) findViewById(R.id.lat_textView);
        mLonTextView = (TextView) findViewById(R.id.lon_textView);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /*
        * Set the location mode to device if it hasn't been set yet.
        */
        String locationMode = prefs.getString(Constants.PREF_LOCATION_MODE, "");

        if ("".equals(locationMode))  {
            prefs.edit()
                    .putString(Constants.PREF_LOCATION_MODE, Constants.PREF_LOCATION_MODE_DEVICE)
                    .commit();
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
    public void onStart()  {
        Log.i(TAG, "onStart() called");

        super.onStart();
        mGoogleApiClient.connect();
//        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop()  {
        Log.i(TAG, "onStop() called");

        mGoogleApiClient.disconnect();
//        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onResume()  {
        Log.i(TAG, "onResume() called");

        super.onResume();
//        updateLocation();

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

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//
//        StringBuilder msg = new StringBuilder();
//        msg.append("Preferences Changed. key=");
//        msg.append(key);
//        msg.append(" value=");
//
//        switch (key) {
//
//            case Constants.PREF_LATITUDE:
//            case Constants.PREF_LONGITUDE:
//                msg.append(String.valueOf(sharedPreferences.getFloat(key, 0F)));
//                break;
//
//            default:
//                msg.append("Unknown key");
//        }
//
//        Log.i(TAG, msg.toString());
//
//        updateLocation();
//
//    }

    private void updateLocation()  {

        String locationMode = prefs.getString(Constants.PREF_LOCATION_MODE, "");

        double lat = 0d;
        double lon = 0d;

        switch (locationMode) {

            case Constants.PREF_LOCATION_MODE_SPECIFY:

                lat = prefs.getFloat(Constants.PREF_LATITUDE, 0f);
                lon = prefs.getFloat(Constants.PREF_LONGITUDE, 0f);

                break;

            case Constants.PREF_LOCATION_MODE_DEVICE:

                if (mLastDeviceLocation != null) {

                    lat = mLastDeviceLocation.getLatitude();
                    lon = mLastDeviceLocation.getLongitude();
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown location mode : " + locationMode);
        }

        mLatTextView.setText(String.valueOf(lat));
        mLonTextView.setText(String.valueOf(lon));

        Intent intent = new Intent(getApplicationContext(), SearchService.class);
        intent.putExtra(Constants.INTENT_LONGITUDE, lon);
        intent.putExtra(Constants.INTENT_LATITUDE, lat);
        startService(intent);


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
