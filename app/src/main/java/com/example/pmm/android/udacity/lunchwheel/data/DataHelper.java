package com.example.pmm.android.udacity.lunchwheel.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;

    static final String DATABASE_NAME = "wheel.db";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_RESTAURANT_TABLE =
                "CREATE TABLE " + DataContract.RestaurantEntry.TABLE_NAME + " (" +
                        DataContract.RestaurantEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DataContract.RestaurantEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        DataContract.RestaurantEntry.COLUMN_URL + " TEXT, " +
                        DataContract.RestaurantEntry.COLUMN_IMAGE + " TEXT, " +
                        DataContract.RestaurantEntry.COLUMN_COORD_LON + " REAL, " +
                        DataContract.RestaurantEntry.COLUMN_COORD_LAT + " REAL, " +
                        DataContract.RestaurantEntry.COLUMN_ADDRESS + " TEXT, " +
                        DataContract.RestaurantEntry.COLUMN_PHONE + " TEXT, " +
                        DataContract.RestaurantEntry.COLUMN_SELECTED + " INTEGER DEFAULT 0" +
                        ")";

        db.execSQL(CREATE_RESTAURANT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)  {
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.RestaurantEntry.TABLE_NAME);
        onCreate(db);
    }
}
