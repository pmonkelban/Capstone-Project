package com.example.pmm.android.udacity.lunchwheel;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SetLocationFragment extends Fragment {

    private static final String TAG = SetLocationFragment.class.getSimpleName();

    private RadioButton mUseDeviceLocationRadioButton;
    private RadioButton mSpecifyLocationRadioButton;

    private View mSpecifyLocationFields;
    private EditText mCityField;
    private EditText mStateField;
    private EditText mZipField;

    private Button mLookupAddressButton;

    private float mLatitude;
    private float mLongitude;

    private LocationResultsReceiver mLocationResultsReceiver;

    private SharedPreferences prefs;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mLocationResultsReceiver = new LocationResultsReceiver(new Handler());


        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.set_location_fragment, container, false);

        mSpecifyLocationFields = view.findViewById(R.id.specify_location_fields);

        mCityField = (EditText) view.findViewById(R.id.city_editText);
        mStateField = (EditText) view.findViewById(R.id.state_editText);
        mZipField = (EditText) view.findViewById(R.id.zip_editText);

        // Load values from last use
        mCityField.setText(prefs.getString(Constants.PREF_CITY, ""));
        mStateField.setText(prefs.getString(Constants.PREF_STATE, ""));
        mZipField.setText(prefs.getString(Constants.PREF_ZIP, ""));


        mLookupAddressButton = (Button) view.findViewById(R.id.lookup_address_button);

        mLookupAddressButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(),
                        FetchLocationIntentService.class);

                intent.putExtra(Constants.RECEIVER, mLocationResultsReceiver);

                StringBuilder sb = new StringBuilder();
                sb.append(mCityField.getText().toString());

                if ((mStateField.getText().toString() != null) &&
                        (mStateField.getText().toString().trim().length() > 0)) {
                    sb.append(", ");
                    sb.append(mStateField.getText().toString().trim().toUpperCase());
                }

                if ((mZipField.getText().toString() != null) &&
                        (mZipField.getText().toString().trim().length() > 0)) {
                    sb.append(" ");
                    sb.append(mZipField.getText().toString());
                }

                intent.putExtra(Constants.LOCATION_ADDRESS_EXTRA, sb.toString());
                getActivity().startService(intent);

                prefs.edit()
                        .putString(Constants.PREF_CITY, mCityField.getText().toString())
                        .putString(Constants.PREF_STATE, mStateField.getText().toString())
                        .putString(Constants.PREF_ZIP, mZipField.getText().toString())
                        .commit();

            }
        });

        mUseDeviceLocationRadioButton = (RadioButton) view.findViewById(R.id.use_device_location_button);
        mSpecifyLocationRadioButton = (RadioButton) view.findViewById(R.id.specify_location_button);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSpecifyLocationRadioButton.isChecked()) {
                    mSpecifyLocationFields.setVisibility(View.VISIBLE);
                    prefs.edit()
                            .putString(Constants.PREF_LOCATION_MODE, Constants.PREF_LOCATION_MODE_SPECIFY)
                            .commit();
                } else {
                    mSpecifyLocationFields.setVisibility(View.GONE);
                    prefs.edit()
                            .putString(Constants.PREF_LOCATION_MODE, Constants.PREF_LOCATION_MODE_DEVICE)
                            .commit();
                }

                setLocation();
            }
        };

        mUseDeviceLocationRadioButton.setOnClickListener(onClickListener);
        mSpecifyLocationRadioButton.setOnClickListener(onClickListener);

        if (prefs.getString(Constants.PREF_LOCATION_MODE, "")
                .equals(Constants.PREF_LOCATION_MODE_DEVICE)) {

            mUseDeviceLocationRadioButton.setChecked(true);
            mSpecifyLocationFields.setVisibility(View.GONE);

        } else {
            mSpecifyLocationRadioButton.setChecked(true);
            mSpecifyLocationFields.setVisibility(View.VISIBLE);

        }

        return view;

    }

    private void setLocation() {

        prefs.edit()
                .putFloat(Constants.PREF_LATITUDE, mLatitude)
                .putFloat(Constants.PREF_LONGITUDE, mLongitude)
                .commit();

    }


    class LocationResultsReceiver extends ResultReceiver {

        public LocationResultsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {
                mLongitude = (float) resultData.getDouble(Constants.RESULT_LONGITUDE);
                mLatitude = (float) resultData.getDouble(Constants.RESULT_LATITUDE);

                Toast.makeText(getActivity(),
                        "Location Updated",
                        Toast.LENGTH_SHORT).show();

                setLocation();

            } else {
                Toast.makeText(getActivity(),
                        resultData.getString(Constants.RESULT_STRING),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
