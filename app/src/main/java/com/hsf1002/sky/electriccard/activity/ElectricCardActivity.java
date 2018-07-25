package com.hsf1002.sky.electriccard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
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
        boolean isSimcardActivated = resultInfo.getFlag();
        String dateTime = resultInfo.getTime();

        if (!isSimcardActivated)
        {
            Log.d(TAG, "setTextViev: electric card not activated.............................");
            electricStrDefault = getString(R.string.activate_message, year, month, day, hour, minute, second);
            textView.setText(electricStrDefault);
            return;
        }

        if (TextUtils.isEmpty(dateTime)) {
            Log.d(TAG, "setTextViev: does not receive the first msg from provider........");
            electricStrDefault = getString(R.string.activate_message, year, month, day, hour, minute, second);
            textView.setText(electricStrDefault);
            return;
        }
        else
        {
            // 20180723205009
            if (dateTime.length() != 14) {
                Log.d(TAG, "setTextViev: dataTime read from file length error............");
                return;
            }

            year = dateTime.substring(0, 4);
            month = dateTime.substring(4, 6);
            day = dateTime.substring(6, 8);
            hour = dateTime.substring(8, 10);
            minute = dateTime.substring(10, 12);
            second = dateTime.substring(12, 14);
            electricStrDefault = getString(R.string.activate_message, year, month, day, hour, minute, second);
            textView.setText(electricStrDefault);
        }
    }
}
