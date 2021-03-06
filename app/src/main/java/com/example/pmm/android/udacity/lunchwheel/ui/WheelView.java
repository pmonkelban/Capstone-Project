package com.example.pmm.android.udacity.lunchwheel.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;

import com.example.pmm.android.udacity.lunchwheel.data.DataProvider;

import java.util.ArrayList;
import java.util.List;

public class WheelView extends AdapterView<CursorAdapter> {

    private static String MAX_STRING_LEN = "This is really the longest label allowed";
    private static final int INNER_PADDING = 75;
    private static final int OUTER_PADDING = 25;

    /*
    * TODO: Max length should be based on the space available, not the number of characters.
    */
    public static final int MAX_NAME_LENGTH = 20;

    private final Rect mTextBounds = new Rect();

    private DataSetObserver observer = new DataSetObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            updateData();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            onChanged();
        }
    };

    GestureDetectorCompat mGestureDetector;

    List<String> names;

    public static final String TAG = WheelView.class.getCanonicalName();

    private CursorAdapter mAdapter;

    private Paint mTextPaint;
    private Paint mBorderPaint;

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.setWillNotDraw(false);
    }

    @Override
    public CursorAdapter getAdapter() {
        return null;
    }

    @Override
    public void setAdapter(CursorAdapter adapter) {
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(observer);

    }

    @Override
    public View getSelectedView() {
        Log.d(TAG, "getSelectedView()");
        return null;
    }

    @Override
    public void setSelection(int position) {
        throw new UnsupportedOperationException("setSelection() Not Supported");
    }

    private void updateData() {

        names = new ArrayList<>();

        if (mAdapter == null) return;

        Cursor c = mAdapter.getCursor();
        if (c == null) return;

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String name = c.getString(DataProvider.RESTAURANT_INDEX_NAME);
            if (name.length() > MAX_NAME_LENGTH)  {
                name = name.substring(0, MAX_NAME_LENGTH) + "...";
            }
            names.add(name);
            c.moveToNext();
        }

        invalidate();

    }

    private void init() {

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setTextSize(15f);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setTextSize(15f);

    }

    protected void onDraw(Canvas canvas) {

        Log.d(TAG, "onDraw() called");

        if (names == null) {
            Log.d(TAG, "onDraw() names is null");
            return;
        }

        Log.d(TAG, "onDraw() names.size=" + names.size());

        float centerX = canvas.getWidth() / 2f;
        float centerY = canvas.getHeight() / 2f;

        float minDimension = Math.min(canvas.getWidth(), canvas.getHeight());

        float radius = minDimension / 2f;

        float rotDeltaDegrees = 360f / (float) names.size() / 2f;

        for (int i = 0; i < names.size(); i++) {
            canvas.drawLine(centerX, centerY, (centerX + radius), centerY, mBorderPaint);
            canvas.rotate(rotDeltaDegrees, centerX, centerY);

            float textLen = mTextPaint.measureText(names.get(i));
            float textStartX = (centerX + radius) - OUTER_PADDING - textLen;
            textStartX = Math.max(textStartX, centerX + INNER_PADDING);

            mTextPaint.getTextBounds(names.get(i), 0, names.get(i).length(), mTextBounds);
            canvas.drawText(names.get(i), textStartX, centerY - mTextBounds.exactCenterY(), mTextPaint);

            canvas.rotate(rotDeltaDegrees, centerX, centerY);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent() event:" + event);

        if (mGestureDetector != null)  {
            mGestureDetector.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // Get the width measurement
        int widthSize = getMeasurement(widthMeasureSpec, getDesiredWidth());
        int heightSize = getMeasurement(heightMeasureSpec, getDesiredHeight());

        setMeasuredDimension(widthSize, heightSize);

    }

    private static int getMeasurement(int measureSpec, int contentSize) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        int resultSize = 0;
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                resultSize = contentSize;
                break;
            case View.MeasureSpec.AT_MOST:
                resultSize = Math.min(contentSize, specSize);
                break;
            case View.MeasureSpec.EXACTLY:
                resultSize = specSize;
                break;
        }

        return resultSize;
    }

    private int getDesiredWidth() {

        // It's a square.
        return getDesiredHeight();
    }

    private int getDesiredHeight() {

        int h = 0;

        h = (int) (mTextPaint.measureText(MAX_STRING_LEN) + 0.5)
                + INNER_PADDING
                + OUTER_PADDING;

        return 2 * h;
    }

}


