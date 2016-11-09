package com.fredrik.wakeup.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fredrik.wakeup.R;
import com.fredrik.wakeup.interfaces.TimestampRemovedListener;
import com.fredrik.wakeup.other.Database;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AlarmsAdapter  extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {
    private long[] alarmTimes;
    private Database database;

    private TimestampRemovedListener timestampRemovedListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AlarmsAdapter(TimestampRemovedListener timestampRemovedListener) {
        this.timestampRemovedListener = timestampRemovedListener;
    }

    public void setData(long[] alarmTimes){
        this.alarmTimes = alarmTimes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AlarmsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final long alarmTimestamp = alarmTimes[position];
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm", Locale.US);
        String textRep = sdf.format(alarmTimestamp);
        holder.mTextView.setText(textRep);

        holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            private long theAlarmTimestamp = alarmTimestamp;

            @Override
            public boolean onLongClick(View view) {
                timestampRemovedListener.onTimestampRemoved(theAlarmTimestamp);
                return true;
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(alarmTimes == null){
            return 0;
        }
        return alarmTimes.length;
    }
}