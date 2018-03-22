package com.thirdtou;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.thirdtou.Weather.MinutelyWeather;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018-02-27.
 */

public class NotificationExampleActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {




    //GPSTracker
    GPSTracker gps = null;

    double latitude;
    double longitude;


    //배경 setting하기
    ImageView background;
    UserData userData;
    SharedPreference sharedPreference = new SharedPreference();
    String user_name, user_birth;
    long startDate;

    TextView currentdate, celcious, content1, content2, currentTime;
    Typeface typeface1, typeface2, typeface3;
    ImageView weatherIcon, icon;
    Button button_touch;

    TimeHandler timeHandler;
    Timer timer;
    TimerTask timerTask;
    //checkNum위한 함쉬
    int yesterday_i;
    String curTime;
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    //setBackground 위한 random 변수 선언
    int rainyNum;
    int randomNum;
    int randomNumAug;
    int randomNumMarch;
    int randomNumMay;
    int randomNumJuly;
    int randomNumOct;
    int randomNumNov;
    int randomNumSep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationexample);

        LockApplication.activities.add(this);

        getData();
        showComponet();
        setTypeface();
        setDate();
        timeHandler = new TimeHandler(this);

        startRealTimeTimer();
        setIcon();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }


    }


    private void startRealTimeTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                curTime = timeFormat.format(new Date());
                timeHandler.obtainMessage().sendToTarget();
            }
        };
        timer.schedule(timerTask, 0, 10 * 1000);

    }

    private void updateTimeText() {
        currentTime.setText(curTime);
    }

    //GPS code


    public void GPSEvent() {
        if (gps == null) {
            bindService(new Intent(this,GPSTracker.class),this,BIND_AUTO_CREATE);
        } else {
            gps.Update();
        }

        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            getWeather(latitude, longitude);
            // \n is for new line
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    //화면 초기화
    private void showComponet() {

        currentTime = findViewById(R.id.current_time);
        background = findViewById(R.id.backgroud);
        currentdate = findViewById(R.id.currentdate);
        celcious = findViewById(R.id.celcious);
        content1 = findViewById(R.id.content_upper);
        content2 = findViewById(R.id.content_below);
        button_touch = findViewById(R.id.button_touch);
        weatherIcon = findViewById(R.id.weatherimage);
        icon = findViewById(R.id.icon);

        button_touch.setOnClickListener(this);
    }

    /**
     *
     */

    private void setDate() {
        Date date = new Date();
        SimpleDateFormat formatdate = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat formatmonth = new SimpleDateFormat("MMM", Locale.ENGLISH);

        String str1 = formatdate.format(date);
        String str2 = formatmonth.format(date).toUpperCase(Locale.ENGLISH);
        String result = str1 + " " + str2;
        currentdate.setText(result);

    }

    private void setIcon() {

        Glide.with(this).load(R.drawable.fillingicon)//날씨 정보에 맞는 날씨 이미지 넣어야 함.
                .into(icon);

    }

    public void setTypeface() {
        typeface1 = Typeface.createFromAsset(getAssets(), "fonts/smr.ttf");
        typeface2 = Typeface.createFromAsset(getAssets(), "fonts/jejug.ttf");
        typeface3 = Typeface.createFromAsset(getAssets(), "fonts/nanumg.ttf");

//        button_exit.setTypeface(typeface3);
        currentTime.setTypeface(typeface3);
        currentdate.setTypeface(typeface2);
        celcious.setTypeface(typeface2);
        content1.setTypeface(typeface1);
        content2.setTypeface(typeface1);

    }

    private void setWeatherIcon(String skycode) {
        Log.d("event123",skycode);
        if (skycode.equals("SKY_A01")) {
            weatherIcon.setScaleY(1.1f);
            weatherIcon.setScaleX(1.1f);
            setImage(R.drawable.sky_a01, true);
        } else if (skycode.equals("SKY_A02")) {
            weatherIcon.setScaleX(1.6f);
            weatherIcon.setScaleY(1.6f);
            setImage(R.drawable.sky_a02, false);
        } else if (skycode.equals("SKY_A03")) {
            weatherIcon.setScaleY(1.1f);
            weatherIcon.setScaleX(1.1f);
            setImage(R.drawable.sky_a03, false);
        } else if (skycode.equals("SKY_A04")) {
            weatherIcon.setScaleY(1.5f);
            weatherIcon.setScaleX(1.5f);
            setImage(R.drawable.sky_a04, false);
        } else if (skycode.equals("SKY_A05")) {
            weatherIcon.setScaleY(0.9f);
            weatherIcon.setScaleX(0.9f);
            setImage(R.drawable.sky_a05, false);
        } else if (skycode.equals("SKY_A06")) {
            weatherIcon.setScaleY(0.9f);
            weatherIcon.setScaleX(0.9f);
            setImage( R.drawable.sky_a06, false);
        } else if (skycode.equals("SKY_A07")) {
            weatherIcon.setScaleY(1.6f);
            weatherIcon.setScaleX(1.6f);
            setImage(R.drawable.sky_a07, false);
        } else if (skycode.equals("SKY_A08")) {
            weatherIcon.setScaleY(1.5f);
            weatherIcon.setScaleX(1.5f);
            setImage(R.drawable.sky_a08, false);
        } else if (skycode.equals("SKY_A09")) {
            weatherIcon.setScaleY(0.9f);
            weatherIcon.setScaleX(0.9f);
            setImage(R.drawable.sky_a09, false);
        } else if (skycode.equals("SKY_A10")) {
            weatherIcon.setScaleY(0.9f);
            weatherIcon.setScaleX(0.9f);
            setImage(R.drawable.sky_a10, false);
        } else if (skycode.equals("SKY_A11")) {
            setImage(R.drawable.sky_a11, false);
        } else if (skycode.equals("SKY_A12") || skycode.equals("SKY_A14")) {
            weatherIcon.setScaleY(0.9f);
            weatherIcon.setScaleX(0.9f);
            setImage(R.drawable.sky_a12, false);
        } else if (skycode.equals("SKY_A13")) {
            weatherIcon.setScaleY(0.9f);
            weatherIcon.setScaleX(0.9f);
            setImage(R.drawable.sky_a13, false);
        }
    }

    private void setImage(int resource, boolean b) {

        Glide.with(this).load(resource).into(weatherIcon);

    }

    @Override
    public void onAttachedToWindow() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onAttachedToWindow();

    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, GPSTracker.class));
        bindService(new Intent(this,GPSTracker.class),this,BIND_AUTO_CREATE);
        ((LockApplication) getApplication()).lockScreenShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((LockApplication) getApplication()).lockScreenShow = false;
    }

    //날씨 정보 받아오는 코드
    private void getWeather(double latitude, double longtitude) {
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiService.BASEURL).build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<MinutelyWeather> call = apiService.getMinutely(ApiService.APPKEY, 1, latitude, longtitude);
        call.enqueue(new Callback<MinutelyWeather>() {

            @Override
            public void onResponse(Call<MinutelyWeather> call, Response<MinutelyWeather> response) {

                if (response.isSuccessful()) {
                    //날씨데이터를 받아옴
                    MinutelyWeather object = response.body();
                    String skyCode = object.getWeather().getMinutely().get(0).getSky().getCode();
                    String temper = object.getWeather().getMinutely().get(0).getTemperature().getTc();

                    float temp1 = Float.parseFloat(temper);
                    float result = Math.round(temp1 * 10f) / 10f;
                    temper = String.valueOf(result + "C");

                    if (object != null) {
                        //데이터가 null이 아니면 날씨 데이터 텍스트 뷰로 보여주기
                        //setCurrent_time();
                        setBackground(skyCode);
                        celcious.setText(temper);
                        setWeatherIcon(skyCode);

                    }
                }


            }

            @Override
            public void onFailure(Call<MinutelyWeather> call, Throwable t) {
            }
        });

    }

    private void getData() {
        String data = sharedPreference.getValue(this, "userData", "");
        Gson gson = new Gson();
        userData = gson.fromJson(data, UserData.class);
        if (userData != null) {
            user_name = userData.getName();
            user_name = user_name.replace(" ", "");
            user_birth = userData.getBirth();
            startDate = userData.getStartdate();
        }
    }

    public void setBackground(String skyCode) {

        Date start = new Date();
        start.setTime(startDate);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
        String startSt = formatter.format(start);
        SimpleDateFormat formatter2 = new SimpleDateFormat("MM월dd일", Locale.ENGLISH);
        SimpleDateFormat formatter3 = new SimpleDateFormat("MMM", Locale.ENGLISH);

        try {
            Date date = formatter2.parse(user_birth);
            String birth = formatter.format(date);
            String[] specialDay = {startSt, birth, "24 Sep", "07 Aug"};
            Date today = new Date();
            today.setTime(System.currentTimeMillis());
            String todaySt = formatter.format(today);
            String thisMonth = formatter3.format(today);

            Resources res = getResources();
            String[] specialImgs = res.getStringArray(R.array.specialday);

            if (todaySt.equals(specialDay[0])) {
                Glide.with(this).load(specialImgs[0]).into(background);
            } else if (todaySt.equals(specialDay[1])) {
                Glide.with(this).load(specialImgs[1]).into(background);
            } else if (todaySt.equals(specialDay[2])) {
                Glide.with(this).load(specialImgs[2]).into(background);
            } else if (todaySt.equals(specialDay[3])) {
                Glide.with(this).load(specialImgs[3]).into(background);
            } else {
                setWeatherBackground(skyCode, thisMonth, today);
            }
        } catch (ParseException e) {
        }


    }

    private void setWeatherBackground(String skycode, String thisMonth, Date date) {

        Date checkdate = new Date();
        //비올때 아이콘 랜덤하게 배경 출력
        Resources res = getResources();
        String[] rainyImgs = res.getStringArray(R.array.rainIcon);
        String[] ranImgs = res.getStringArray(R.array.randomBack);
        String[] ranAugImgs = res.getStringArray(R.array.randomAug);
        String[] ranMarchImgs = res.getStringArray(R.array.randomMarch);
        String[] ranMayImgs = res.getStringArray(R.array.randomMay);
        String[] ranJulyImgs = res.getStringArray(R.array.randomJuly);
        String[] ranOctImgs = res.getStringArray(R.array.randomOct);
        String[] ranNovImgs = res.getStringArray(R.array.randomNov);
        String[] ranSepImgs = res.getStringArray(R.array.randomSep);

        //Random random = new Random();

        if(!checkdate.equals(date)) {
            //다른 날이면 randomNum 바꾸기
            Random random = new Random();
            rainyNum = random.nextInt( rainyImgs.length-1);
            randomNum = random.nextInt(ranImgs.length - 1);
            randomNumAug = random.nextInt(ranAugImgs.length - 1);
            randomNumMarch = random.nextInt( ranMarchImgs.length - 1);
            randomNumMay = random.nextInt(ranMayImgs.length - 1);
            randomNumJuly = random.nextInt(ranJulyImgs.length - 1);
            randomNumOct = random.nextInt(ranOctImgs.length - 1);
            randomNumNov = random.nextInt(ranNovImgs.length - 1);
            randomNumSep = random.nextInt( ranNovImgs.length - 1);
            rainyNum = checkRandomNum(rainyNum, rainyImgs.length-1);
            randomNumAug = checkRandomNum(randomNumAug, ranAugImgs.length-1);
            randomNumMarch = checkRandomNum(randomNumMarch, ranMarchImgs.length-1);
            randomNumMay = checkRandomNum(randomNumMay, ranMayImgs.length-1);
            randomNumJuly = checkRandomNum(randomNumJuly, ranJulyImgs.length-1);
            randomNumSep = checkRandomNum(randomNumSep, ranSepImgs.length-1);
            randomNumOct = checkRandomNum(randomNumOct, ranOctImgs.length-1);
            randomNumNov = checkRandomNum(randomNumNov, ranNovImgs.length-1);
            randomNum = checkRandomNum(randomNum, ranImgs.length-1);

            if (skycode.equals("SKY_A04") || skycode.equals("SKY_A08")||skycode.equals("SKY_A06") ||
                    skycode.equals("SKY_A10")||skycode.equals("SKY_A12")||skycode.equals("SKY_A14")) {
                setImage(rainyImgs[rainyNum]);
            } else {
                //simpledatecode pasing 한게 MMM aug,march,may,july,oct,nov)
                if (thisMonth.equals("Aug")) {

                    setImage(ranAugImgs[randomNumAug]);
                } else if (thisMonth.equals("Mar")) {

                    if (randomNumMarch == 0) {
                        content1.setText(user_name + "님 아직 날씨가 쌀쌀하네요.");
                        content2.setText("감기 조심하세요.");
                    }
                    setImage(ranMarchImgs[randomNumMarch]);
                } else if (thisMonth.equals("May")) {

                    if (randomNumMay == 0) {
                        content1.setText(user_name);
                        content2.setText("늦게까지 수고 많았어요.");
                    }
                    setImage(ranMayImgs[randomNumMay]);
                } else if (thisMonth.equals("Jul")) {

                    setImage(ranJulyImgs[randomNumJuly]);
                } else if (thisMonth.equals("Sep")) {

                    if (randomNumSep == 0) {
                        content1.setText(user_name + "님 ");
                        content2.setText("걱정없는 밤 되세요.");
                    }
                    setImage(ranSepImgs[randomNumSep]);
                } else if (thisMonth.equals("Oct")) {

                    setImage(ranOctImgs[randomNumOct]);
                } else if (thisMonth.equals("Nov")) {

                    setImage(ranOctImgs[randomNumNov]);
                } else {

                    setImage(ranImgs[randomNum]);
                }
            }
        }else{
            int rainyNum = getRainyNum();
            int randomNum = getRandomNum();
            int randomNumAug = getRandomNumAug();
            int randomNumMarch = getRandomNumMarch();
            int randomNumMay = getRandomNumMay();
            int randomNumJuly = getRandomNumJuly();
            int randomNumOct = getRandomNumOct();
            int randomNumNov = getRandomNumNov();
            int randomNumSep = getRandomNumSep();

            if (skycode.equals("SKY_A04") || skycode.equals("SKY_A08")||skycode.equals("SKY_A06") ||
                    skycode.equals("SKY_A10")||skycode.equals("SKY_A12")||skycode.equals("SKY_A14")) {
                setImage(rainyImgs[rainyNum]);
            } else {
                //simpledatecode pasing 한게 MMM aug,march,may,july,oct,nov)
                if (thisMonth.equals("Aug")) {

                    setImage(ranAugImgs[randomNumAug]);
                } else if (thisMonth.equals("Mar")) {

                    if (randomNumMarch == 0) {
                        content1.setText(user_name + "님 아직 날씨가 쌀쌀하네요.");
                        content2.setText("감기 조심하세요.");
                    }
                    setImage(ranMarchImgs[randomNumMarch]);
                } else if (thisMonth.equals("May")) {

                    if (randomNumMay == 0) {
                        content1.setText(user_name);
                        content2.setText("늦게까지 수고 많았어요.");
                    }
                    setImage(ranMayImgs[randomNumMay]);
                } else if (thisMonth.equals("Jul")) {

                    setImage(ranJulyImgs[randomNumJuly]);
                } else if (thisMonth.equals("Sep")) {

                    if (randomNumSep == 0) {
                        content1.setText(user_name + "님 ");
                        content2.setText("걱정없는 밤 되세요.");
                    }
                    setImage(ranSepImgs[randomNumSep]);
                } else if (thisMonth.equals("Oct")) {

                    setImage(ranOctImgs[randomNumOct]);
                } else if (thisMonth.equals("Nov")) {

                    setImage(ranOctImgs[randomNumNov]);
                } else {

                    setImage(ranImgs[randomNum]);
                }
            }
        }
    }
    private int getRainyNum(){
        return rainyNum;
    }
    private int getRandomNum(){
        return randomNum;
    }
    private int getRandomNumAug(){
        return randomNumAug;
    }
    private int getRandomNumMarch(){
        return randomNumMarch;
    }
    private int getRandomNumMay(){
        return randomNumMay;
    }
    private int getRandomNumJuly(){
        return randomNumJuly;
    }
    private int getRandomNumOct(){
        return randomNumOct;
    }
    private int getRandomNumNov(){
        return randomNumNov;
    }
    private int getRandomNumSep(){
        return randomNumSep;
    }


    private int checkRandomNum(int today_i, int arraySize) {
        Random random = new Random();
        if (yesterday_i == today_i) {
            today_i++;
            if (today_i > arraySize) {
                today_i = random.nextInt(arraySize-1);
            }
        }
        yesterday_i = today_i;
        return today_i;
    }


    private void setImage(String resource) {
        Glide.with(this).load(resource).into(background);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_touch) {
            finish();
        }

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        GPSTracker.GpsBinder binder = (GPSTracker.GpsBinder) iBinder;
        gps = binder.getService();
        GPSEvent();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    private static class TimeHandler extends Handler {
        private final WeakReference<NotificationExampleActivity> activities;

        private TimeHandler(NotificationExampleActivity activities) {
            this.activities = new WeakReference<NotificationExampleActivity>(activities);
        }

        @Override
        public void handleMessage(Message msg) {
            activities.get().updateTimeText();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
        if(gps!=null){
            gps.stopUsingGPS();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
            timeHandler = null;
            timerTask = null;
        }
    }
}

