package com.example.pmm.android.udacity.lunchwheel.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pmm.android.udacity.lunchwheel.Constants;
import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.data.DataContract;
import com.example.pmm.android.udacity.lunchwheel.data.DataProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = ResultsActivity.class.getCanonicalName();

    public static final int DEFAULT_MAP_ZOOM = 14;

    float mEndLat;
    float mEndLon;

    float mStartLat;
    float mStartLon;

    String mResultName;

    boolean mMapReady = false;
    GoogleMap mGmap;

    private MenuItem mShareMenuItem;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        mStartLon = intent.getFloatExtra(Constants.INTENT_LONGITUDE, 0f);
        mStartLat = intent.getFloatExtra(Constants.INTENT_LATITUDE, 0f);

        final String RESULT_QUERY_WHERE_CLAUSE =
                DataContract.RestaurantEntry.COLUMN_ID + " = ?";

        Cursor c = null;

        try {
            c = getApplicationContext().getContentResolver().query(
                    DataContract.RestaurantEntry.CONTENT_URI,
                    null,
                    RESULT_QUERY_WHERE_CLAUSE,
                    new String[]{intent.getStringExtra(Constants.INTENT_RESULT_ID)},
                    null);

            if (c != null) {
                c.moveToFirst();
                mEndLat = Float.valueOf(c.getString(DataProvider.RESTAURANT_INDEX_LAT));
                mEndLon = Float.valueOf(c.getString(DataProvider.RESTAURANT_INDEX_LON));
                mResultName = c.getString(DataProvider.RESTAURANT_INDEX_NAME);
            }

        } finally  {

            if ((c != null) && (!c.isClosed()))  {
                c.close();
            }
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.result_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_results, menu);

        mShareMenuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mShareMenuItem);
        mShareActionProvider.setShareIntent(createShareTrackIntent());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onMapReady(GoogleMap map)  {
        mMapReady = true;
        mGmap = map;

        LatLng startLocation = new LatLng(mStartLat, mStartLon);
        LatLng endLocation = new LatLng(mEndLat, mEndLon);

        CameraPosition cameraPosition = CameraPosition
                .builder()
                .target(endLocation)
                .zoom(DEFAULT_MAP_ZOOM)
                .build();

        mGmap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        MarkerOptions startMarkerOptions = new MarkerOptions()
                .position(startLocation)
                .draggable(false)
                .title(getString(R.string.start));

        mGmap.addMarker(startMarkerOptions);

        MarkerOptions endMarkerOptions = new MarkerOptions()
                .position(endLocation)
                .draggable(false)
                .title(mResultName);

        mGmap.addMarker(endMarkerOptions);

    }

    protected Intent createShareTrackIntent() {

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String shareMsg = "Meet me here for lunch:";

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        shareIntent.setType("text/plain");

        return shareIntent;

    }

}
