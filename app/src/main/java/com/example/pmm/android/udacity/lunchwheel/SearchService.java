package com.example.pmm.android.udacity.lunchwheel;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class SearchService extends IntentService {

    private static final String TAG = FetchLocationIntentService.class.getSimpleName();

    private static final Object syncLock = new Object();

    private static final String API_HOST = "api.yelp.com";
    private static final String SEARCH_TERM = "restaurants";
    private static final String DEFAULT_LOCATION = "40.7577,-73.9857"; // Times Square
    private static final int SEARCH_LIMIT = 3;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    private String CONSUMER_KEY;
    private String CONSUMER_SECRET;
    private String TOKEN;
    private String TOKEN_SECRET;

    OAuthService service;
    Token accessToken;

    public SearchService()  {
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

        if ((latitude == 0d) || (longitude == 0d))  {
            location = DEFAULT_LOCATION;
        } else {
            location = String.valueOf(latitude) + "," + String.valueOf(longitude);
        }

        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", SEARCH_TERM);
        request.addQuerystringParameter("ll", location);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        String response = sendRequestAndGetResponse(request);

        Log.i(TAG, "Yelp API Response: " + response);

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
