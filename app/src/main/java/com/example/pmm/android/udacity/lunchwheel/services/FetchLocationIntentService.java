package com.example.pmm.android.udacity.lunchwheel.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.pmm.android.udacity.lunchwheel.Constants;
import com.example.pmm.android.udacity.lunchwheel.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchLocationIntentService extends IntentService {

    private static final String TAG = FetchLocationIntentService.class.getSimpleName();

    private static final int MAX_LOCATION_RESULTS = 1;

    protected ResultReceiver mReceiver;

    public FetchLocationIntentService()  {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)  {

        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        String address = intent.getStringExtra(Constants.LOCATION_ADDRESS_EXTRA);

        if ((address == null) || (address.trim().length() == 0))  {
            errorMessage = getString(R.string.error_no_location_data);
            Log.wtf(TAG, errorMessage);
            sendResultsToReceiver(Constants.FAILURE_RESULT, errorMessage, null);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(address, MAX_LOCATION_RESULTS);

        } catch (IOException ioException)  {

            errorMessage = getString(R.string.error_address_lookup_unavailable);
            Log.e(TAG, errorMessage, ioException);

        } catch (IllegalArgumentException illegalArgException)  {

            errorMessage = getString(R.string.error_illegal_address_lookup_arg);
            Log.e(TAG, errorMessage + " address=[" + address + "]", illegalArgException);

        }

        if ((addresses == null) || (addresses.size() == 0))  {

            if (errorMessage.isEmpty())  {
                errorMessage = getString(R.string.error_address_not_found);
                Log.e(TAG, errorMessage);
            }

            sendResultsToReceiver(Constants.FAILURE_RESULT, errorMessage, null);

        } else  {

            Address result = addresses.get(0);
            String resultStr = getString(R.string.location_found);
            Log.i(TAG, resultStr);

            sendResultsToReceiver(Constants.SUCCESS_RESULT, resultStr, result);
        }


    }

    private void sendResultsToReceiver(int resultCode, String resultStr, Address address) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.RESULT_CODE, resultCode);
        bundle.putString(Constants.RESULT_STRING, resultStr);

        if (resultCode == Constants.SUCCESS_RESULT) {
            bundle.putDouble(Constants.RESULT_LONGITUDE, address.getLongitude());
            bundle.putDouble(Constants.RESULT_LATITUDE, address.getLatitude());
        }

        mReceiver.send(resultCode, bundle);
    }

}
