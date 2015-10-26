package com.example.pmm.android.udacity.lunchwheel.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.activities.MainActivity;
import com.example.pmm.android.udacity.lunchwheel.activities.ResultsActivity;
import com.example.pmm.android.udacity.lunchwheel.data.DataContract;
import com.example.pmm.android.udacity.lunchwheel.data.DataProvider;

public class WidgetIntentService extends IntentService {

    private static final String TAG = WidgetIntentService.class.getCanonicalName();

    final String RESULT_QUERY_WHERE_CLAUSE =
            DataContract.RestaurantEntry.COLUMN_SELECTED + " = 1";

    public WidgetIntentService()  {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)  {

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));

        String name = null;
        String address = null;
        String phone = null;

        boolean isDataAvailable = false;


        Cursor c = null;

        /*
        * Get data associated with the most recently selected result.
        */
        try {
            c = getContentResolver().query(
                    DataContract.RestaurantEntry.CONTENT_URI,
                    null,
                    RESULT_QUERY_WHERE_CLAUSE,
                    null,
                    null);

            if ((c != null) && (c.getCount() > 0)) {
                c.moveToFirst();
                name = c.getString(DataProvider.RESTAURANT_INDEX_NAME);
                address = c.getString(DataProvider.RESTAURANT_INDEX_ADDRESS);
                phone = c.getString(DataProvider.RESTAURANT_INDEX_PHONE);
                isDataAvailable = true;
            }

        } finally {

            if ((c != null) && (!c.isClosed())) {
                c.close();
            }
        }

        for (int appWidgetId : appWidgetIds)  {
            int layoutId = R.layout.widget_detail;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.result_name, name);
            views.setTextViewText(R.id.result_address, address);
            views.setTextViewText(R.id.result_phone, phone);

            PendingIntent pendingIntent;

            if (isDataAvailable)  {

                pendingIntent = TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(new Intent(this, MainActivity.class))
                        .addNextIntent(new Intent(this, ResultsActivity.class))
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            } else  {

                pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(this, MainActivity.class),
                        0);

            }


            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

    }
}
