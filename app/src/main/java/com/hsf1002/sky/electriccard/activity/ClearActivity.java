package com.hsf1002.sky.electriccard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.hsf1002.sky.electriccard.R;
import com.hsf1002.sky.electriccard.utils.SavePrefsUtils;

/**
 * Created by hefeng on 18-7-19.
 */

public class ClearActivity extends Activity {
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new View(this));
        initDialog();
    }

    public void initDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(ClearActivity.this);
        builder.setTitle(R.string.clear_alert_title)
        .setIcon(R.mipmap.ic_launcher)
        .setMessage(R.string.clear_alert_content)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                SavePrefsUtils.handleClearActivatedState();
                finish();
            }
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog = builder.create();

        if(alertDialog !=null && !alertDialog.isShowing())
        {
            alertDialog.show();
        }
    }
}
