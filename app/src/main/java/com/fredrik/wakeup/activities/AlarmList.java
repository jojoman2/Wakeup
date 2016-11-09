package com.fredrik.wakeup.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.fredrik.wakeup.R;
import com.fredrik.wakeup.adapters.AlarmsAdapter;
import com.fredrik.wakeup.broadcastreceivers.AlarmBroadcastReceiver;
import com.fredrik.wakeup.interfaces.TimestampRemovedListener;
import com.fredrik.wakeup.other.Database;

public class AlarmList extends AppCompatActivity {

    private AlarmsAdapter alarmsAdapter;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        database = new Database(this);
        database.removeAlarmsOlderThan(System.currentTimeMillis());

        RecyclerView alarmsRecyclerView = (RecyclerView) findViewById(R.id.alarmsList);
        View newAlarmButton = findViewById(R.id.newAlarmButton);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        alarmsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        alarmsRecyclerView.setLayoutManager(mLayoutManager);



        alarmsAdapter = new AlarmsAdapter(new TimestampRemovedListener() {
            @Override
            public void onTimestampRemoved(final long timestamp) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmList.this);
                builder.setItems(new String[]{"Remove"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.removeSpecificAlarm(timestamp);
                        updateAdapter();

                        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
                        intent.setData(Uri.parse("custom://" + timestamp));
                        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
                        AlarmManager alarmManager = (AlarmManager)AlarmList.this.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pi);

                    }
                });
                AppCompatDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

            }
        });

        alarmsRecyclerView.setAdapter(alarmsAdapter);





        newAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmList.this, SetupTime.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateAdapter();
    }

    private void updateAdapter(){

        long[] alarmTimes = database.getAlarmTimestamps();
        alarmsAdapter.setData(alarmTimes);
        alarmsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
