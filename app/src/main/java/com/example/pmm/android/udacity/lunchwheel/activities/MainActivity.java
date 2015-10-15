package com.example.pmm.android.udacity.lunchwheel.activities;

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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.pmm.android.udacity.lunchwheel.Constants;
import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.data.DataContract;
import com.example.pmm.android.udacity.lunchwheel.data.DataProvider;
import com.example.pmm.android.udacity.lunchwheel.services.SearchService;
import com.example.pmm.android.udacity.lunchwheel.ui.WheelAdapter;
import com.example.pmm.android.udacity.lunchwheel.ui.WheelView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean mIsTwoPanel = false;

    SharedPreferences prefs;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastDeviceLocation;

    private WheelView mWheelView;
    private WheelAdapter mWheelAdapter;
    private Button mSpinButton;

    public static final int RESTAURANT_LOADER_ID = 0;

    Random rnd;

    Animation mRotateWheelAnimation;

    Intent mShowResultsIntent;

//    public static final double REFRESH_DISTANCE_METERS = 100d;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSpinButton = (Button) findViewById(R.id.spin_wheel_button);
        mSpinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSpinWheel();
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mSpinButton.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode)  {
                Log.e(TAG, "Ad failed to load.  errorCode="+errorCode);
                super.onAdFailedToLoad(errorCode);

                /*
                * If the Ad failed to load, still allow the user to proceed.
                */
                mSpinButton.setEnabled(true);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
                showResults();
            }
        });

        requestNewInterstitial();

        rnd = new Random(System.currentTimeMillis());

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setDefaultPreferences();

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

        // Create a new WheelAdapter and bind it to the ListView
        mWheelAdapter = new WheelAdapter(this, null, 0);
        mWheelView = (WheelView) findViewById(R.id.restaurants_listView);
        mWheelView.setAdapter(mWheelAdapter);

        getLoaderManager().initLoader(RESTAURANT_LOADER_ID, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {

                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                        return new CursorLoader(MainActivity.this,
                                DataContract.RestaurantEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        mWheelAdapter.swapCursor(data);
                        mWheelAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                        mWheelAdapter.swapCursor(null);
                        mWheelAdapter.notifyDataSetChanged();
                    }

                });

        // Create animation to spin the wheel
        mRotateWheelAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_wheel);

        mRotateWheelAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (mInterstitialAd.isLoaded())  {
                    mInterstitialAd.show();
                } else  {
                    requestNewInterstitial();
                    showResults();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private void setDefaultPreferences() {

        if (!prefs.contains(Constants.PREF_LOCATION_MODE)) {
            prefs.edit()
                    .putString(Constants.PREF_LOCATION_MODE, Constants.PREF_LOCATION_MODE_DEVICE)
                    .commit();
        }

        if (!prefs.contains(Constants.PREF_SEARCH_RADIUS_IN_MILES)) {
            prefs.edit()
                    .putInt(Constants.PREF_SEARCH_RADIUS_IN_MILES, Constants.DEFAULT_SEARCH_RADIUS)
                    .commit();
        }

        if (!prefs.contains(Constants.PREF_MIN_RATING)) {
            prefs.edit()
                    .putFloat(Constants.PREF_MIN_RATING, Constants.DEFAULT_MIN_RATING)
                    .commit();
        }
    }

    private void doSpinWheel()  {
        mSpinButton.setEnabled(false);
        mWheelView.startAnimation(mRotateWheelAnimation);
    }

    private void showResults() {

        Cursor c = null;

        try {
            c = getApplicationContext().getContentResolver().query(
                    DataContract.RestaurantEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            if ((c == null) || (c.getCount() == 0)) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.toast_no_results),
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            int randomPositon = rnd.nextInt(c.getCount());

            c.moveToPosition(randomPositon);

            Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
            intent.putExtra(Constants.INTENT_RESULT_ID, c.getString(DataProvider.RESTAURANT_INDEX_ID));

        /*
        * Add the starting location to the intent.
        * Check the location mode (either device or specified) and pull
        * the preference accordingly.
        */
            if (Constants.PREF_LOCATION_MODE_DEVICE.equals(prefs.getString(Constants.PREF_LOCATION_MODE, ""))) {
                intent.putExtra(Constants.INTENT_LONGITUDE, prefs.getFloat(Constants.PREF_DEVICE_LONGITUDE, 0f));
                intent.putExtra(Constants.INTENT_LATITUDE, prefs.getFloat(Constants.PREF_DEVICE_LATITUDE, 0f));
            } else {
                intent.putExtra(Constants.INTENT_LONGITUDE, prefs.getFloat(Constants.PREF_SPECIFIED_LONGITUDE, 0f));
                intent.putExtra(Constants.INTENT_LATITUDE, prefs.getFloat(Constants.PREF_SPECIFIED_LATITUDE, 0f));
            }

            startActivity(intent);

        } finally  {

            if ((c != null) && (!c.isClosed()))  {
                c.close();
            }
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
                startActivity(new Intent(this, PreferencesActivity.class));
                break;

            case R.id.action_location:
                startActivity(new Intent(this, LocationActivity.class));
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

        /*
        * If the location is specified, then we don't need to wait for the google api client
        * to connect.  Go ahead and update the location with the info we already have.
        */
        if (Constants.PREF_LOCATION_MODE_SPECIFY.equals(prefs.getString(Constants.PREF_LOCATION_MODE, ""))) {
            updateLocation();
        }
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

        float lon = (mLastDeviceLocation == null) ? 0f : (float) mLastDeviceLocation.getLongitude();
        float lat = (mLastDeviceLocation == null) ? 0f : (float) mLastDeviceLocation.getLatitude();

        prefs.edit()
                .putFloat(Constants.PREF_DEVICE_LATITUDE, lat)
                .putFloat(Constants.PREF_DEVICE_LONGITUDE, lon)
                .commit();

        if (
                (Constants.PREF_LOCATION_MODE_DEVICE.equals(prefs.getString(Constants.PREF_LOCATION_MODE, "")))
                        && (mLastDeviceLocation == null)
                ) {

            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.toast_no_device_location),
                    Toast.LENGTH_LONG)
                    .show();
        } else {

//          if (Util.distance(old_lat, lat, old_lon, lon, 0d, 0d) > REFRESH_DISTANCE_METERS) {
            SearchService.updateSearchResults(this);
//          }
        }
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
