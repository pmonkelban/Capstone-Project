package com.example.pmm.android.udacity.lunchwheel;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.example.pmm.android.udacity.lunchwheel.data.DataContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;

public class SearchService extends IntentService {

    private static final String TAG = FetchLocationIntentService.class.getSimpleName();

    private static final Object syncLock = new Object();

    private static final String API_HOST = "api.yelp.com";
    private static final String SEARCH_TERM = "restaurants";
    private static final String DEFAULT_LOCATION = "40.7577,-73.9857"; // Times Square
    private static final String SEARCH_LIMIT = "20";
    private static final String SEARCH_RADIUS = "16100"; // 5 miles
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    private String CONSUMER_KEY;
    private String CONSUMER_SECRET;
    private String TOKEN;
    private String TOKEN_SECRET;

    OAuthService service;
    Token accessToken;

    public SearchService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        synchronized (syncLock) {

            if (service == null) {

                CONSUMER_KEY = getResources().getString(R.string.yelp_consumer_key);
                CONSUMER_SECRET = getResources().getString(R.string.yelp_consumer_secret);
                TOKEN = getResources().getString(R.string.yelp_token);
                TOKEN_SECRET = getResources().getString(R.string.yelp_token_secret);

                this.service = new ServiceBuilder()
                        .provider(TwoStepOAuth.class)
                        .apiKey(CONSUMER_KEY)
                        .apiSecret(CONSUMER_SECRET)
                        .build();

                this.accessToken = new Token(TOKEN, TOKEN_SECRET);
            }
        }

        double latitude = intent.getDoubleExtra(Constants.INTENT_LATITUDE, 0d);
        double longitude = intent.getDoubleExtra(Constants.INTENT_LONGITUDE, 0d);
        String location;

        if ((latitude == 0d) || (longitude == 0d)) {
            location = DEFAULT_LOCATION;
        } else {
            location = String.valueOf(latitude) + "," + String.valueOf(longitude);
        }

        OAuthRequest request1 = createOAuthRequest(SEARCH_PATH);
        request1.addQuerystringParameter("term", SEARCH_TERM);
        request1.addQuerystringParameter("ll", location);
        request1.addQuerystringParameter("limit", SEARCH_LIMIT);
        request1.addQuerystringParameter("radius_filter", SEARCH_RADIUS);
        request1.addQuerystringParameter("sort", String.valueOf(2));
        String response1 = sendRequestAndGetResponse(request1);

        OAuthRequest request2 = createOAuthRequest(SEARCH_PATH);
        request2.addQuerystringParameter("term", SEARCH_TERM);
        request2.addQuerystringParameter("ll", location);
        request2.addQuerystringParameter("limit", SEARCH_LIMIT);
        request2.addQuerystringParameter("radius_filter", SEARCH_RADIUS);
        request2.addQuerystringParameter("sort", String.valueOf(2));
        request2.addQuerystringParameter("offset", String.valueOf(20));
        String response2 = sendRequestAndGetResponse(request2);

        Log.i(TAG, "Yelp API Response_1: " + response1);
        Log.i(TAG, "Yelp API Response_2: " + response2);

        // Remove all existing Restaurant entries
        getApplicationContext().getContentResolver().delete(DataContract.RestaurantEntry.CONTENT_URI, null, null);

        List<ContentValues> cv1 = insertRestaurantEntriesFromJSON(response1);
        List<ContentValues> cv2 = insertRestaurantEntriesFromJSON(response2);

        ContentValues[] cvArray = new ContentValues[cv1.size() + cv2.size()];

        int x = 0;

        for (int i = 0; i < cv1.size(); i++)  {
            cvArray[x++] = cv1.get(i);
        }

        for (int i = 0; i < cv2.size(); i++)  {
            cvArray[x++] = cv2.get(i);
        }

        getApplicationContext().getContentResolver().bulkInsert(DataContract.RestaurantEntry.CONTENT_URI, cvArray);


    }

    private List<ContentValues> insertRestaurantEntriesFromJSON(String str) {

        final String JSON_BUSINESSES = "businesses";
        final String JSON_LOCATION = "location";
        final String JSON_COORDINATE = "coordinate";

        final String JSON_NAME = "name";
        final String JSON_URL = "mobile_url";
        final String JSON_IMAGE = "image_url";
        final String JSON_LAT = "latitude";
        final String JSON_LON = "longitude";

        List<ContentValues> cvList = new ArrayList<>();

        try {
            JSONObject searchResults = new JSONObject(str);

            JSONArray businesses = searchResults.getJSONArray(JSON_BUSINESSES);

            for (int i = 0; i < businesses.length(); i++) {

                JSONObject json_business = businesses.getJSONObject(i);

                ContentValues values = new ContentValues();

                if (json_business.has(JSON_NAME)) {
                    values.put(DataContract.RestaurantEntry.COLUMN_NAME, json_business.getString(JSON_NAME));
                } else {
                    continue;
                }

                if (json_business.has(JSON_URL)) {
                    values.put(DataContract.RestaurantEntry.COLUMN_URL, json_business.getString(JSON_URL));
                }

                if (json_business.has(JSON_IMAGE)) {
                    values.put(DataContract.RestaurantEntry.COLUMN_IMAGE, json_business.getString(JSON_IMAGE));
                }

                JSONObject o;

                if (json_business.has(JSON_LOCATION)) {
                    o = json_business.getJSONObject(JSON_LOCATION);

                    if (o.has(JSON_COORDINATE)) {
                        o = o.getJSONObject(JSON_COORDINATE);

                        if (o.has(JSON_LAT)) {
                            values.put(DataContract.RestaurantEntry.COLUMN_COORD_LONG, o.getString(JSON_LAT));
                        }

                        if (o.has(JSON_LON)) {
                            values.put(DataContract.RestaurantEntry.COLUMN_COORD_LAT, o.getString(JSON_LON));

                        }
                    }

                }

                cvList.add(values);

            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return cvList;

    }

    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    private String sendRequestAndGetResponse(OAuthRequest request) {
        Log.i(TAG, "Querying " + request.getCompleteUrl() + " ...");
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

}
