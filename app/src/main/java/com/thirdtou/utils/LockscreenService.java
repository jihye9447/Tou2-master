package com.thirdtou.utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.thirdtou.LockApplication;
import com.thirdtou.NotificationExampleActivity;
import com.thirdtou.SharedPreference;

import java.util.Date;

/**
 * Created by Administrator on 2018-03-01.
 */

public class LockscreenService extends Service implements LocationListener{




    LocationManager locationManager;
    double longitude, latitude;

    long start_time = 0;
    long end_time = 0;

    RandomNumber randomNumber = new RandomNumber();
    Date date;


    private Context mContext = null;

    SharedPreference sharedPreference = new SharedPreference();


    NotiBinder notiBinder = new NotiBinder();

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        sharedPreference.putLocation(this,"lat",latitude);
        sharedPreference.putLocation(this,"long", longitude);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stateRecever(true);
        requestLocation();

        return LockscreenService.START_STICKY;
    }

    public double getLongitude(){
        return sharedPreference.getLocationValue(this,"long");
    }
    public double getLatitude(){
        return sharedPreference.getLocationValue(this,"lat");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return notiBinder;
    }

    @Override
    public void onDestroy() {
        stateRecever(false);

    }

    private void startLockscreenActivity() {

        Intent startLockscreenActIntent = new Intent(mContext, NotificationExampleActivity.class);
        startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLockscreenActIntent);
    }

    private void destroyActivity(){
        if(LockApplication.activities.size()>0) {
            for (AppCompatActivity activity : LockApplication.activities) {
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

    private void requestLocation(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
            }
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, this);
            }
        }

    }
}
