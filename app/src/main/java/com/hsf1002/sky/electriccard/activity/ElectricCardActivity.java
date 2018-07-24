package com.hsf1002.sky.electriccard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hsf1002.sky.electriccard.R;
import com.hsf1002.sky.electriccard.entity.ResultInfo;
import com.hsf1002.sky.electriccard.utils.SaveFileUtils;

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
        String electricStrDefault = null;
        ResultInfo resultInfo = SaveFileUtils.getInstance().readElectricCardActivated();

        if (resultInfo == null)
        {
            Log.d(TAG, "setTextViev: resultInfo read from file is empty..............................");
        }
        else
        {
            // 20180723205009
            String dateTime = resultInfo.getTime();

            if (dateTime.length() != 14) {
                Log.d(TAG, "setTextViev: dataTime read from file length error..............................");
                return;
            }

            year = dateTime.substring(0, 4);
            month = dateTime.substring(4, 6);
            day = dateTime.substring(6, 8);
            hour = dateTime.substring(8, 10);
            minute = dateTime.substring(10, 12);
            second = dateTime.substring(12, 14);
        }

        electricStrDefault = getString(R.string.activate_message, year, month, day, hour, minute, second);

        Log.d(TAG, "setTextViev: electricStr from shared pref = " + electricStrDefault);

        textView.setText(electricStrDefault);
    }
}
