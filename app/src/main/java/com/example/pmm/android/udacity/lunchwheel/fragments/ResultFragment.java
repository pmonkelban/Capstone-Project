package com.example.pmm.android.udacity.lunchwheel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmm.android.udacity.lunchwheel.Constants;
import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.data.DataContract;
import com.example.pmm.android.udacity.lunchwheel.data.DataProvider;
import com.example.pmm.android.udacity.lunchwheel.services.SearchService;
import com.squareup.picasso.Picasso;

public class ResultFragment extends Fragment {

    private static final String TAG = ResultFragment.class.getSimpleName();

    private TextView mResultName;
    private TextView mResultUrl;
    private ImageView mResultImage;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.result_fragment, container, false);

        Intent intent = getActivity().getIntent();

        final String RESULT_QUERY_WHERE_CLAUSE =
                DataContract.RestaurantEntry.COLUMN_ID + " = ?";

        Cursor c = getActivity().getApplicationContext().getContentResolver().query(
                DataContract.RestaurantEntry.CONTENT_URI,
                null,
                RESULT_QUERY_WHERE_CLAUSE,
                new String[]{intent.getStringExtra(Constants.INTENT_RESULT_ID)},
                null);

        mResultName = (TextView) view.findViewById(R.id.result_name);
        mResultUrl = (TextView) view.findViewById(R.id.result_url);
        mResultImage = (ImageView) view.findViewById(R.id.result_image);

        c.moveToFirst();

        mResultName.setText(c.getString(DataProvider.RESTAURANT_INDEX_NAME));

        final String yelp_url = c.getString(DataProvider.RESTAURANT_INDEX_URL);

        mResultUrl.setText(getString(R.string.yelp_link_description));

        View.OnClickListener clickListener = new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(yelp_url));
                startActivity(intent);
            }
        };

        mResultUrl.setOnClickListener(clickListener);
        mResultImage.setOnClickListener(clickListener);

        String imageUrl = c.getString(DataProvider.RESTAURANT_INDEX_IMAGE);

        if ((imageUrl != null) && (imageUrl.trim().length() > 0))  {
            Picasso.with(getActivity().getApplicationContext())
                    .load(imageUrl)
                    .resize(
                            (int) getResources().getDimension(R.dimen.result_image_width),
                            (int) getResources().getDimension(R.dimen.result_image_height))
                    .into(mResultImage);
        }

        return view;

    }

}
