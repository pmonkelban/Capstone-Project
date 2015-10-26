package com.example.pmm.android.udacity.lunchwheel.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;



public class DataProvider extends ContentProvider {


    public static final int RESTAURANT_INDEX_ID = 0;
    public static final int RESTAURANT_INDEX_NAME = 1;
    public static final int RESTAURANT_INDEX_URL = 2;
    public static final int RESTAURANT_INDEX_IMAGE = 3;
    public static final int RESTAURANT_INDEX_LON = 4;
    public static final int RESTAURANT_INDEX_LAT = 5;
    public static final int RESTAURANT_INDEX_ADDRESS = 6;
    public static final int RESTAURANT_INDEX_PHONE = 7;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final String ACTION_DATA_UPDATED =
            "com.example.pmm.android.udacity.lunchwheel.ACTION_DATA_UPDATED";


    private DataHelper mDataHelper;

    static final int RESTAURANT = 100;
    static final int RESET_SELECTED = 200;

    static UriMatcher buildUriMatcher()  {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DataContract.PATH_RESTAURANT, RESTAURANT);
        return matcher;
    }

    @Override
    public boolean onCreate()  {
        mDataHelper = new DataHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri)  {

        final int match = sUriMatcher.match(uri);

        switch (match)  {
            case RESTAURANT:
                return DataContract.RestaurantEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder)  {

        Cursor retCursor;

        switch (sUriMatcher.match(uri))  {

            case RESTAURANT:
            {
                retCursor = mDataHelper.getReadableDatabase().query(
                        DataContract.RestaurantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values)  {
        final SQLiteDatabase db = mDataHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RESTAURANT: {

                long _id = db.insert(DataContract.RestaurantEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.RestaurantEntry.buildLRestaurantUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDataHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case RESTAURANT:
                rowsDeleted = db.delete(DataContract.RestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0)  {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;

    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDataHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case RESTAURANT:
                rowsUpdated = db.update(DataContract.RestaurantEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

            /*
            * Send this out to update the widget.
            */
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            getContext().sendBroadcast(dataUpdatedIntent);
        }
        return rowsUpdated;
    }
}
