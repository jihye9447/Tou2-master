package com.thirdtou.utils;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;

import com.thirdtou.LockApplication;
import com.thirdtou.NotificationExampleActivity;
import com.thirdtou.SharedPreference;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018-03-01.
 */

public class LockscreenService extends Service{

    private Timer lock_timer = new Timer();
    private TimerTask lock_timerTask;

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

                Log.d("event123",intent.getAction());
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    startLockscreenActivity();

                }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){

                    destroyActivity();
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

    private void destroyActivity(){
        if(LockApplication.activities.size()>0){
            for(AppCompatActivity activity: LockApplication.activities){
                activity.finish();
            }
        }
    }

}
