package com.tou.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tou.LockApplication;
import com.tou.MainActivity;
import com.tou.NotificationExampleActivity;
import com.tou.R;
import com.tou.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018-03-01.
 */

public class LockscreenService extends Service{

    private final String TAG = "LockscreenService";
    private int mServiceStartId = 0;
    private Context mContext = null;
    int count=0;
    SharedPreference sharedPreference = new SharedPreference();
    private NotificationManager mNM;


    private BroadcastReceiver mLockscreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != context) {

                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    //날짜가 하루 지났을때 count를 0으로

                    startLockscreenActivity();
                    /*if (count==0){
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM", Locale.ENGLISH);
                        Date date = new Date();
                        String today = formatter.format(date);
                        String yesterDay = sharedPreference.getValue(context,"today","");
                        if (!yesterDay.equals(today)){
                            sharedPreference.put(context,"today",today);
                            count=0;
                        }
                    }
                    count++;
                    if (count==5){
                        startLockscreenActivity();
                    }
                    Log.d("test123","컷다켜진 횟수 : "+count);
*/
                }
            }
        }
    };

    private void stateRecever(boolean isStartRecever) {
        if (isStartRecever) {
            IntentFilter filter = new IntentFilter();
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
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        stateRecever(true);
        Intent bundleIntet = intent;
        if (null != bundleIntet) {
            // startLockscreenActivity();
            Log.d(TAG, TAG + " onStartCommand intent  existed");
        } else {
            Log.d(TAG, TAG + " onStartCommand intent NOT existed");
        }
        return LockscreenService.START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stateRecever(false);
        mNM.cancel(((LockApplication) getApplication()).notificationId);
    }

    private void startLockscreenActivity() {
        Intent startLockscreenActIntent = new Intent(mContext, NotificationExampleActivity.class);
        startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLockscreenActIntent);
    }

    private void showNotification() {
        CharSequence text = "Running";
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.app_name))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();

        mNM.notify(((LockApplication) getApplication()).notificationId, notification);
    }

}
