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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmm.android.udacity.lunchwheel.Constants;
import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.services.FetchLocationIntentService;
import com.example.pmm.android.udacity.lunchwheel.services.SearchService;

public class PreferencesFragment extends Fragment {

    private static final String TAG = PreferencesFragment.class.getSimpleName();

    private SharedPreferences prefs;

    TextView mMaxDistance;
    TextView mMinRating;
    SeekBar mMaxDistanceSlider;
    SeekBar mMinRatingSlider;
    Button mSavePreferencesButton;

    int mNewMaxDistancePref;
    float mNewMinRatingPref;

    private static final int MAX_DISTANCE = 25;
    private static final int MIN_DISTANCE = 1;

    public static final int MIN_RATING = 0;
    public static final int MAX_RATING = 10;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.preferences_fragment, container, false);

        mMaxDistance = (TextView) view.findViewById(R.id.max_distance_text);
        mMinRating = (TextView) view.findViewById(R.id.min_rating_text);
        mMaxDistanceSlider = (SeekBar) view.findViewById(R.id.max_distance_slider);
        mMinRatingSlider = (SeekBar) view.findViewById(R.id.min_rating_slider);
        mSavePreferencesButton = (Button) view.findViewById(R.id.save_preferences);

        mSavePreferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });


        mMaxDistanceSlider.setMax(MAX_DISTANCE - MIN_DISTANCE);
        int progress =  prefs.getInt(Constants.PREF_SEARCH_RADIUS_IN_MILES, 0) - MIN_DISTANCE;
        mMaxDistanceSlider.setProgress(progress);
        updateMaxDistance(progress);

        mMinRatingSlider.setMax(MAX_RATING - MIN_RATING);
        progress = (int) (prefs.getFloat(Constants.PREF_MIN_RATING, 0) * 2) - MIN_RATING;
        mMinRatingSlider.setProgress(progress);
        updateMinRating(progress);

        mMaxDistanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateMaxDistance(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mMinRatingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateMinRating(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;

    }

    private void savePreferences() {
        Log.i(TAG, "savePreferences() called. maxDistance=" + mNewMaxDistancePref + " minRating=" + mNewMinRatingPref);

        prefs.edit()
                .putInt(Constants.PREF_SEARCH_RADIUS_IN_MILES, mNewMaxDistancePref)
                .putFloat(Constants.PREF_MIN_RATING, mNewMinRatingPref)
                .commit();

        SearchService.updateSearchResults(getActivity());

        Toast.makeText(
                getActivity().getApplicationContext(),
                getString(R.string.preferences_saved),
                Toast.LENGTH_SHORT)
                .show();

    }

    private void updateMaxDistance(int progress)  {
        Log.d(TAG, "Updating max distance.  progress=" + progress);
        mNewMaxDistancePref = progress + MIN_DISTANCE;
        mMaxDistance.setText(getString(R.string.selected_miles, mNewMaxDistancePref));
    }

    private void updateMinRating(int progress)  {
        Log.d(TAG, "Updating min rating.  progress=" + progress);
        mNewMinRatingPref = (float) progress / 2f;
        mMinRating.setText(String.valueOf(mNewMinRatingPref));

    }

}
