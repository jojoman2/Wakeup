package com.fredrik.wakeup.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.fredrik.wakeup.R;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Results extends AppCompatActivity {

    public static final String RESULT_KEY = "results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        final TextView totalAheadView = (TextView)findViewById(R.id.secondsAhead);
        final TextView totalBehindView = (TextView)findViewById(R.id.secondsBehind);

        Bundle inExtras = getIntent().getExtras();
        int[] results = inExtras.getIntArray(RESULT_KEY);
        assert(results != null);

        int totalAhead = 0;
        int totalBehind = 0;
        for (int thisResult : results) {
            if (thisResult >= 0) {
                totalAhead += thisResult;
            }
            else {
                totalBehind += thisResult;
            }
        }

        totalAheadView.setText(Integer.toString(totalAhead));
        if(totalAhead > 0){
            totalAheadView.setTextColor(GREEN);
        }
        totalBehindView.setText(Integer.toString(totalBehind));
        if(totalBehind < 0){
            totalBehindView.setTextColor(RED);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
