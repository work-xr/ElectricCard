package com.hsf1002.sky.electriccard.entity;

/**
 * Created by hefeng on 18-7-23.
 */

public class ResultInfo {

    private boolean flag;
    private String time;

    public ResultInfo() {
        flag = false;
        time = "";
    }

    public ResultInfo(boolean flag) {
        this.flag = flag;
        this.time = "";
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ResultInfo{" +
                "flag=" + flag +
                ", time='" + time + '\'' +
                '}';
    }
}
