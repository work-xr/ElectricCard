package com.hsf1002.sky.electriccard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hsf1002.sky.electriccard.R;

import static com.hsf1002.sky.electriccard.utils.NVUtils.readSimcardActivated;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readSimcardDateTime;

public class ElectricCardActivity extends Activity {
    private static final String TAG = "ElectricCardActivity";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_card);

        textView = (TextView)findViewById(R.id.electric_card_tv);

        setTextViev();
    }

    private void setTextViev()
    {
        String year = "****";
        String month = "**";
        String day = "**";
        String hour = "**";
        String minute = "**";
        String second = "**";
        String electricStr = getString(R.string.activate_message, year, month, day, hour, minute, second);

        if (readSimcardActivated())
        {
            Log.d(TAG, "setTextViev: simcard activated");
            electricStr = readSimcardDateTime();
        }

        Log.d(TAG, "setTextViev: electricStr = " + electricStr);

        textView.setText(electricStr);
    }
}
