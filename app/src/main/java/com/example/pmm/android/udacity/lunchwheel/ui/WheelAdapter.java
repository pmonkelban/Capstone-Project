package com.example.pmm.android.udacity.lunchwheel.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.pmm.android.udacity.lunchwheel.R;
import com.example.pmm.android.udacity.lunchwheel.data.DataProvider;

public class WheelAdapter extends CursorAdapter {

    public static class ViewHolder  {
        public final TextView name;

        public ViewHolder(View view)  {
            name = (TextView) view.findViewById(R.id.wheel_field_name);

        }
    }

    public WheelAdapter(Context context, Cursor c, int flags)  {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.wheel_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.name.setText(cursor.getString(DataProvider.RESTAURANT_INDEX_NAME));

        super.notifyDataSetChanged();
    }
}
