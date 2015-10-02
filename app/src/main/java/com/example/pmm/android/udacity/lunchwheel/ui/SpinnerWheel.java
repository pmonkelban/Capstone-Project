package com.example.pmm.android.udacity.lunchwheel.ui;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.pmm.android.udacity.lunchwheel.R;

/**
 * TODO: document your custom view class.
 */
public class SpinnerWheel extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
