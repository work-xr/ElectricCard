package com.hsf1002.sky.electriccard.utils;

import android.util.Log;

import com.hsf1002.sky.electriccard.entity.ResultInfo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by hefeng on 18-7-23.
 */

public class SaveFileUtils {
    private static final String TAG = "SaveFileUtils";
    private static final String FILE_PATH = "/productinfo/electriccard.conf";
    //private static final String FILE_NAME = "electriccard.conf";

    private static final class Holder
    {
        private static final SaveFileUtils sInstance = new SaveFileUtils();
    }

    public static SaveFileUtils getInstance()
    {
        return Holder.sInstance;
    }

    public void writeElectricCardActivated(ResultInfo resultInfo)
    {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        File file = new File(FILE_PATH/*, FILE_NAME*/);

        if (file.exists())
        {
            file.delete();
        }

        try {
            file.createNewFile();
        }
        catch (IOException e)
        {
            Log.d(TAG, "writeElectricCardActivated: create file failed.......................");
            e.printStackTrace();
        }

        Log.d(TAG, "writeElectricCardActivated: write file start.............................");

        try {
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(String.valueOf(resultInfo.getFlag()).getBytes());
            bufferedOutputStream.write("\n".getBytes());
            bufferedOutputStream.write(resultInfo.getTime().getBytes());
            bufferedOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedOutputStream.close();
                fileOutputStream.close();
                Log.d(TAG, "writeElectricCardActivated: write file success...................");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resetElectricCardActivated() {
        File file = new File(FILE_PATH/*, FILE_NAME*/);

        Log.d(TAG, "resetElectricCardActivated: delete file start............................");
        if (file.exists()) {
            file.delete();
            Log.d(TAG, "resetElectricCardActivated: delete file finished.....................");
        }
    }

    /* 如果文件已经存在, 说明激活标志已写入, 但是激活日期可能为空 */
    public ResultInfo readElectricCardActivated()
    {
        File file = new File(FILE_PATH/*, FILE_NAME*/);
        ResultInfo resultInfo = new ResultInfo(false);

        Log.d(TAG, "readElectricCardActivated: read file start ..............................");

        if (file.isFile() && file.exists())
        {
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;

            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                String line = bufferedReader.readLine();

                //while (line != null) {
                    //content.append(line);
                Log.d(TAG, "readElectricCardActivated: line1 = " + line);

                if (line.contains("false"))
                {
                    resultInfo.setFlag(false);
                }
                else if (line.contains("true"))
                {
                    resultInfo.setFlag(true);
                }
                else
                {
                    resultInfo.setFlag(false);
                }
                line = bufferedReader.readLine();
                Log.d(TAG, "readElectricCardActivated: line2 = " + line);
                resultInfo.setTime(line);
                //}
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.d(TAG, "readElectricCardActivated: read file failed, file not existed .......");
        }

        return resultInfo;
    }
}
