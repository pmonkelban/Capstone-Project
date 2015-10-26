package com.example.pmm.android.udacity.lunchwheel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pmm.android.udacity.lunchwheel.Constants;
import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.data.DataContract;
import com.example.pmm.android.udacity.lunchwheel.data.DataProvider;
import com.squareup.picasso.Picasso;

public class ResultFragment extends Fragment {

    private static final String TAG = ResultFragment.class.getSimpleName();

    private TextView mResultName;
    private TextView mResultUrl;
    private ImageView mResultImage;
    private TextView mResultAddress;
    private TextView mResultPhone;

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
                DataContract.RestaurantEntry.COLUMN_SELECTED + " = 1";

        final String RESULT_QUERY_WHERE_CLAUSE_INTENT =
                DataContract.RestaurantEntry.COLUMN_ID + " = ?";


        Cursor c = null;

        try {

            if (intent.hasExtra(Constants.INTENT_RESULT_ID)) {

                c = getActivity().getApplicationContext().getContentResolver().query(
                        DataContract.RestaurantEntry.CONTENT_URI,
                        null,
                        RESULT_QUERY_WHERE_CLAUSE_INTENT,
                        new String[]{intent.getStringExtra(Constants.INTENT_RESULT_ID)},
                        null);

            } else  {

                c = getActivity().getApplicationContext().getContentResolver().query(
                        DataContract.RestaurantEntry.CONTENT_URI,
                        null,
                        RESULT_QUERY_WHERE_CLAUSE,
                        null,
                        null);

            }

            mResultName = (TextView) view.findViewById(R.id.result_name);
            mResultUrl = (TextView) view.findViewById(R.id.result_url);
            mResultImage = (ImageView) view.findViewById(R.id.result_image);
            mResultAddress = (TextView) view.findViewById(R.id.result_address);
            mResultPhone = (TextView) view.findViewById(R.id.result_phone);


            if (c != null) {

                c.moveToFirst();

                setTextOrDisappear(mResultName, c.getString(DataProvider.RESTAURANT_INDEX_NAME));

                final String yelp_url = c.getString(DataProvider.RESTAURANT_INDEX_URL);

                View.OnClickListener clickListener = new View.OnClickListener() {
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

                if ((imageUrl != null) && (imageUrl.trim().length() > 0)) {
                    Picasso.with(getActivity().getApplicationContext())
                            .load(imageUrl)
                            .resize(
                                    (int) getResources().getDimension(R.dimen.result_image_width),
                                    (int) getResources().getDimension(R.dimen.result_image_height))
                            .into(mResultImage);
                }

                setTextOrDisappear(mResultAddress, c.getString(DataProvider.RESTAURANT_INDEX_ADDRESS));
                setTextOrDisappear(mResultPhone, c.getString(DataProvider.RESTAURANT_INDEX_PHONE));

                /*
                * Bring up the phone dialer if the number is clicked.
                */
                mResultPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + mResultPhone.getText()));
                        startActivity(intent);
                    }
                });

            }

        } finally  {

            if ((c != null) && (!c.isClosed()))  {
                c.close();
            }
        }

        return view;

    }

    private static boolean setTextOrDisappear(TextView v, String s)  {

        if ((s == null) || (s.trim().length() == 0))  {
            v.setVisibility(View.GONE);
            return false;
        }

        v.setText(s);
        return true;

    }

}
