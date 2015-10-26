package com.example.pmm.android.udacity.lunchwheel.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.pmm.android.udacity.lunchwheel.data.DataProvider;

public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getCanonicalName();

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent)  {
        super.onReceive(context, intent);
        if (DataProvider.ACTION_DATA_UPDATED.equals(intent.getAction()))  {
            context.startService(new Intent(context, WidgetIntentService.class));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, WidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, WidgetIntentService.class));
    }

}
