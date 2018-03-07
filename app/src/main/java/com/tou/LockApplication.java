package com.tou;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-03-01.
 */

public class LockApplication extends Application {

    public boolean lockScreenShow=false;
    public int notificationId=1989;
    static ArrayList<AppCompatActivity> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

}