package com.example.pmm.android.udacity.lunchwheel.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {


    public static final String CONTENT_AUTHORITY = "com.example.pmm.android.udacity.lunchwheel";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RESTAURANT = "restaurants";




    /* Inner class that defines the table contents of the location table */
    public static final class RestaurantEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTAURANT;

        public static final String TABLE_NAME = "restaurant";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_IMAGE = "imageUrl";
        public static final String COLUMN_COORD_LON = "coord_long";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_ADDRESS = "address_1";
        public static final String COLUMN_ADDR_2 = "address_2";
        public static final String COLUMN_PHONE = "phone";

        public static Uri buildLRestaurantUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
