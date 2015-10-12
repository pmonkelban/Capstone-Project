package com.example.pmm.android.udacity.lunchwheel.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.pmm.android.udacity.lunchwheel.R;

public class SpinnerWheel extends AbsListView {

    public static final String TAG = SpinnerWheel.class.getCanonicalName();


    private float mDiameter;
    private Paint mTextPaint;

    public SpinnerWheel(Context context, AttributeSet attrs)  {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SpinnerWheel,
                0, 0);

        try  {
            mDiameter = a.getFloat(R.styleable.SpinnerWheel_diameter, 0f);
        } finally  {
            a.recycle();
        }

        init();

    }

    @Override
    public ListAdapter getAdapter() {
        Log.d(TAG, "getAdapter() called");
        return null;
    }

    @Override
    public void setSelection(int position) {
        Log.d(TAG, "setSelection() called.  position=" + position);

    }

    public float getmDiameter() {
        return mDiameter;
    }

    public void setmDiameter(float mDiameter) {
        this.mDiameter = mDiameter;
        invalidate();
        requestLayout();
    }

    private void init()  {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setTextSize(15f);
    }

    protected void onDraw(Canvas canvas)  {
        canvas.drawText("Hello World", 10f, 10f, mTextPaint);
    }


}

