package com.hsf1002.sky.electriccard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.hsf1002.sky.electriccard.R;
import com.hsf1002.sky.electriccard.service.ElectricCardService;
import com.hsf1002.sky.electriccard.utils.NVutils;

import static com.hsf1002.sky.electriccard.utils.Constant.IS_ELECTRIC_CARD_ACTIVATED;
import static com.hsf1002.sky.electriccard.utils.NVutils.getSimcardActivated;

public class ElectricCardActivity extends Activity {

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

        if (getSimcardActivated())
        {
            textView.setText(electricStr);
        }
        else
        {
            textView.setText(electricStr);
        }
    }
}
