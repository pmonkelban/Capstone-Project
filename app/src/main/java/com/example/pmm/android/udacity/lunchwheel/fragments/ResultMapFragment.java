package com.example.pmm.android.udacity.lunchwheel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.pmm.android.udacity.lunchwheel.Constants;
import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.services.SearchService;

public class ResultMapFragment extends Fragment {

    private static final String TAG = ResultMapFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.result_map_fragment, container, false);

        return view;

    }

}
