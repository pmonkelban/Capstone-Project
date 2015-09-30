package com.example.pmm.android.udacity.lunchwheel;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class SetLocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = SetLocationFragment.class.getSimpleName();

    private TextView mLatTextView;
    private TextView mLonTextView;

    private EditText mCityField;
    private EditText mStateField;
    private Button mLookupAddressButton;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean mLocationAvailable = false;

    private LocationResultsReceiver mLocationResultsReceiver;


    @Override
    public void onCreate(Bundle bundle)  {
        super.onCreate(bundle);

        mLocationResultsReceiver = new LocationResultsReceiver(new Handler());

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.set_location, container, false);

        mLatTextView = (TextView) view.findViewById(R.id.lat_textView);
        mLonTextView = (TextView) view.findViewById(R.id.lon_textView);

        mCityField = (EditText) view.findViewById(R.id.city_editText);
        mStateField = (EditText) view.findViewById(R.id.state_editText);
        mLookupAddressButton = (Button) view.findViewById(R.id.lookup_address_button);


        mLookupAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(),
                        FetchLocationIntentService.class);

                intent.putExtra(Constants.RECEIVER, mLocationResultsReceiver);

                String address = mCityField.getText() + ", " + mStateField.getText();
                intent.putExtra(Constants.LOCATION_ADDRESS_EXTRA, address);
                getActivity().startService(intent);

            }
        });

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected() called");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null)  {
            mLatTextView.setText(String.valueOf(mLastLocation.getLatitude()));
            mLonTextView.setText(String.valueOf(mLastLocation.getLongitude()));
            mLocationAvailable = true;
        }

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

    class LocationResultsReceiver extends ResultReceiver  {

        public LocationResultsReceiver(Handler handler)  {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)  {

            if (resultCode == Constants.SUCCESS_RESULT)  {
                mLatTextView.setText(String.valueOf(resultData.getDouble(Constants.RESULT_LATITUDE)));
                mLonTextView.setText(String.valueOf(resultData.getDouble(Constants.RESULT_LONGITUDE)));

                Toast.makeText(getActivity(),
                        "Location Updated",
                        Toast.LENGTH_SHORT).show();

            } else  {
                Toast.makeText(getActivity(),
                        resultData.getString(Constants.RESULT_STRING),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
