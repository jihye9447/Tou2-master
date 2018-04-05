package com.thirdtou.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;

import com.google.gson.Gson;
import com.thirdtou.ChecknameActivity;
import com.thirdtou.LockApplication;
import com.thirdtou.NotificationExampleActivity;
import com.thirdtou.SharedPreference;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;

/**
 * Created by Administrator on 2018-03-01.
 */

public class LockscreenService extends Service{


    Timer lock_timer = new Timer();
    TimerTask lock_timerTask;


    long start_time = 0;
    long end_time = 0;

    RandomNumber randomNumber = new RandomNumber();
    Date date;


    private final String TAG = "LockscreenService";
    private int mServiceStartId = 0;
    private Context mContext = null;
    int count=0;
    SharedPreference sharedPreference = new SharedPreference();
    private NotificationManager mNM;

    NotiBinder notiBinder = new NotiBinder();

    public class NotiBinder extends Binder {
        public LockscreenService getService(){
            return LockscreenService.this;
        }
    }
    private BroadcastReceiver mLockscreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != context) {

                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    start_time = System.currentTimeMillis();
                    startLockscreenActivity();
                    Log.d("event123","start_time:"+String.valueOf(start_time));
                    /*if(Math.abs(start_time-end_time)>10){
                        LockApplication.activities.add();
                    }*/
                }
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    end_time = System.currentTimeMillis();
                    Log.d("event123","end_time"+String.valueOf(end_time));
                }


            }
        }
    };

    private void stateRecever(boolean isStartRecever) {
        if (isStartRecever) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(mLockscreenReceiver, filter);

        } else {
            if (null != mLockscreenReceiver) {

                unregisterReceiver(mLockscreenReceiver);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        stateRecever(true);

        return LockscreenService.START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return notiBinder;
    }

    @Override
    public void onDestroy() {
        stateRecever(false);
        mNM.cancel(((LockApplication) getApplication()).notificationId);
    }

    private void startLockscreenActivity() {

        Intent startLockscreenActIntent = new Intent(mContext, NotificationExampleActivity.class);
        startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLockscreenActIntent);
    }

    private void destroyActivity(){
        if(LockApplication.activities.size()>0){
            for(AppCompatActivity activity: LockApplication.activities){
                activity.finish();
            }
        }
    }

    public void saveDate(Date date){
        sharedPreference.put(getApplicationContext(),"date",date.getTime());
    }

    public void saveRandomNumber(RandomNumber randomNumber){
        sharedPreference.put(getApplicationContext(),"randomNumber",randomNumber);
    }

    public RandomNumber getRandomNumber() {
        randomNumber = sharedPreference.getValue(getApplicationContext(),"randomNumber",RandomNumber.class);
        return randomNumber;
    }

    public void setRandomNumber(RandomNumber randomNumber) {
        saveRandomNumber(randomNumber);
    }

    public Date getDate() {
        long time = sharedPreference.getValue(getApplicationContext(),"date", 0L);
        if(time==0){
            return null;
        }else{
            date = new Date();
            date.setTime(time);
            return date;
        }
    }

    public void setDate(Date date) {
        saveDate(date);
    }
}
