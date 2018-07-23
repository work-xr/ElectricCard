package com.hsf1002.sky.electriccard.utils;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by hefeng on 18-7-23.
 */

public class SaveFileUtils {
    private static final String TAG = "SaveFileUtils";
    private static final String FILE_PATH = "productinfo";
    private static final String FILE_NAME = "electriccard.conf";

    private static final class Holder
    {
        private static final SaveFileUtils sInstance = new SaveFileUtils();
    }

    public static SaveFileUtils getInstance()
    {
        return Holder.sInstance;
    }

    public void writeElectricCardActivated(boolean activated, String time)
    {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        Log.d(TAG, "writeElectricCardActivated: write file start.......................");

        try {
            fileOutputStream = new FileOutputStream(new File(FILE_PATH, FILE_NAME));
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(String.valueOf(activated).getBytes());
            bufferedOutputStream.write(time.getBytes());
            bufferedOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedOutputStream.close();
                fileOutputStream.close();
                Log.d(TAG, "writeElectricCardActivated: write file success.......................");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String readElectricCardActivated()
    {
        StringBuilder content = null;
        File file = new File(FILE_PATH, FILE_NAME);

        Log.d(TAG, "readElectricCardActivated: read file start .......................");

        if (file.isFile() && file.exists())
        {
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;

            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                String line = bufferedReader.readLine();

                while (line != null) {
                    content.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileReader.close();
                    bufferedReader.close();
                    Log.d(TAG, "readElectricCardActivated: read file success....................... content = " + content.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.d(TAG, "readElectricCardActivated: read file failed, file not existed .......................");
        }

        return content.toString();
    }
}
