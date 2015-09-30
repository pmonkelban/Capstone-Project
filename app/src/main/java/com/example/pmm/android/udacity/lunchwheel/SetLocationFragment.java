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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class SetLocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SetLocationFragment.class.getSimpleName();

    private RadioButton mUseDeviceLocationRadioButton;
    private RadioButton mSpecifyLocationRadioButton;

    private TextView mLatTextView;
    private TextView mLonTextView;

    private View mSpecifyLoctionFields;
    private EditText mCityField;
    private EditText mStateField;
    private Button mLookupAddressButton;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastDeviceLocation;
    private Location mLastSpecifiedLocation = new Location("");

    private boolean mLocationAvailable = false;

    private LocationResultsReceiver mLocationResultsReceiver;


    @Override
    public void onCreate(Bundle bundle) {
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

        mSpecifyLoctionFields = view.findViewById(R.id.specify_location_fields);

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

        mUseDeviceLocationRadioButton = (RadioButton) view.findViewById(R.id.use_device_location_button);
        mSpecifyLocationRadioButton = (RadioButton) view.findViewById(R.id.specify_location_button);

        mUseDeviceLocationRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationType(((RadioButton) v).isChecked());
            }
        });

        mSpecifyLocationRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationType(!((RadioButton) v).isChecked());
            }
        });

        setLocationType(true);

        return view;

    }

    private void setLocationType(boolean useDevice)  {

        if (useDevice)  {
            mUseDeviceLocationRadioButton.setChecked(true);
            mSpecifyLocationRadioButton.setChecked(false);
            mSpecifyLoctionFields.setVisibility(View.GONE);

        } else  {
            mUseDeviceLocationRadioButton.setChecked(false);
            mSpecifyLocationRadioButton.setChecked(true);
            mSpecifyLoctionFields.setVisibility(View.VISIBLE);
        }

        setLonLatText();
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

        mLastDeviceLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        setLonLatText();
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

    class LocationResultsReceiver extends ResultReceiver {

        public LocationResultsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {
                mLastSpecifiedLocation.setLongitude(resultData.getDouble(Constants.RESULT_LONGITUDE));
                mLastSpecifiedLocation.setLatitude(resultData.getDouble(Constants.RESULT_LATITUDE));

                Toast.makeText(getActivity(),
                        "Location Updated",
                        Toast.LENGTH_SHORT).show();

                setLonLatText();

            } else {
                Toast.makeText(getActivity(),
                        resultData.getString(Constants.RESULT_STRING),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setLonLatText() {

        Location l = (mUseDeviceLocationRadioButton.isChecked())
                ? mLastDeviceLocation : mLastSpecifiedLocation;

        if (l != null) {
            mLatTextView.setText(String.valueOf(l.getLatitude()));
            mLonTextView.setText(String.valueOf(l.getLongitude()));

            mLocationAvailable = true;
        } else {
            mLatTextView.setText("?");
            mLonTextView.setText("?");
            mLocationAvailable = false;
        }

    }

}
