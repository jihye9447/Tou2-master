package com.finaltou;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.finaltou.Weather.MinutelyWeather;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018-02-27.
 */

public class NotificationExampleActivity extends AppCompatActivity implements View.OnClickListener {


    //GPSTracker
    GPSTracker gps = null;

    public Handler mHandler;

    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_notificationexample);

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

        //이코드도 방금처럼 바꿔주면 좋아요
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == RENEW_GPS) {
                    makeNewGpsService();
                }
                if (msg.what == SEND_PRINT) {
                    logPrint((String) msg.obj);
                }
            }
        };


        GPSEvent();

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
            gps = new GPSTracker(getApplicationContext(), mHandler);
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

    public void makeNewGpsService() {
        if (gps == null) {
            gps = new GPSTracker(NotificationExampleActivity.this, mHandler);
        } else {
            gps.Update();
        }

    }

    public void logPrint(String str) {

        Date date2 = new Date();

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
            setImage(R.drawable.sky_a06, false);
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<MinutelyWeather> call, Response<MinutelyWeather> response) {

                if (response.isSuccessful()) {
                    //날씨데이터를 받아옴
                    MinutelyWeather object = response.body();
                    String skyCode = object.getWeather().getMinutely().get(0).getSky().getCode();
                    String temper = object.getWeather().getMinutely().get(0).getTemperature().getTc();

                    //startTimerThread();

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
            if (todaySt.equals(specialDay[0])) {
                setImage(R.drawable.firstday);
            } else if (todaySt.equals(specialDay[1])) {
                setImage(R.drawable.birthday);
            } else if (todaySt.equals(specialDay[2])) {
                setImage(R.drawable.sep_24th);
            } else if (todaySt.equals(specialDay[3])) {
                setImage(R.drawable.aug_7th);
            } else {
                setWeatherBackground(skyCode, thisMonth);
            }
        } catch (ParseException e) {
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setWeatherBackground(String skycode, String thisMonth) {
        //비올때 아이콘 랜덤하게 배경 출력
        TypedArray rainyImgs = getResources().obtainTypedArray(R.array.rainIcon);
        TypedArray ranImgs = getResources().obtainTypedArray(R.array.randomBack);
        TypedArray ranAugImgs = getResources().obtainTypedArray(R.array.randomAug);
        TypedArray ranMarchImgs = getResources().obtainTypedArray(R.array.randomMarch);
        TypedArray ranMayImgs = getResources().obtainTypedArray(R.array.randomMay);
        TypedArray ranJulyImgs = getResources().obtainTypedArray(R.array.randomJuly);
        TypedArray ranOctImgs = getResources().obtainTypedArray(R.array.randomOct);
        TypedArray ranNovImgs = getResources().obtainTypedArray(R.array.randomNov);
        TypedArray ranSepImgs = getResources().obtainTypedArray(R.array.randomSep);
        int rainyNum = ThreadLocalRandom.current().nextInt(0, rainyImgs.getIndexCount() + 1);
        int randomNum = ThreadLocalRandom.current().nextInt(0, ranImgs.getIndexCount() + 1);
        int randomNumAug = ThreadLocalRandom.current().nextInt(0, ranAugImgs.getIndexCount() + 1);
        int randomNumMarch = ThreadLocalRandom.current().nextInt(0, ranMarchImgs.getIndexCount() + 1);
        int randomNumMay = ThreadLocalRandom.current().nextInt(0, ranMayImgs.getIndexCount() + 1);
        int randomNumJuly = ThreadLocalRandom.current().nextInt(0, ranJulyImgs.getIndexCount() + 1);
        int randomNumOct = ThreadLocalRandom.current().nextInt(0, ranOctImgs.getIndexCount() + 1);
        int randomNumNov = ThreadLocalRandom.current().nextInt(0, ranNovImgs.getIndexCount() + 1);
        int randomNumSep = ThreadLocalRandom.current().nextInt(0, ranNovImgs.getIndexCount() + 1);

        if (skycode.equals("SKY_A04") || skycode.equals("SKY_A08")) {
            rainyNum = checkRandomNum(rainyNum, rainyImgs.getIndexCount() + 1);
            setImage(rainyImgs.getResourceId(rainyNum, -1));
        } else if (skycode.equals("SKY_A06") || skycode.equals("SKY_A10")) {
            rainyNum = checkRandomNum(rainyNum, rainyImgs.getIndexCount() + 1);
            setImage(rainyImgs.getResourceId(rainyNum, -1));
        } else if (skycode.equals("SKY_A12")) {
            //thunder, rainy
            rainyNum = checkRandomNum(rainyNum, rainyImgs.getIndexCount() + 1);
            setImage(rainyImgs.getResourceId(rainyNum, -1));
        } else if (skycode.equals("SKY_A14")) {
            rainyNum = checkRandomNum(rainyNum, rainyImgs.getIndexCount() + 1);
            setImage(rainyImgs.getResourceId(rainyNum, -1));
            //vector rainy,snowy-5,thuder
        } else {
            //simpledatecode pasing 한게 MMM aug,march,may,july,oct,nov)
            if (thisMonth.equals("Aug")) {
                randomNumAug = checkRandomNum(randomNumAug, ranAugImgs.getIndexCount() + 1);
                setImage(ranAugImgs.getResourceId(randomNumAug, -1));
            } else if (thisMonth.equals("Mar")) {
                randomNumMarch = checkRandomNum(randomNumMarch, ranMarchImgs.getIndexCount() + 1);
                if (randomNumMarch == 0) {
                    content1.setText(user_name + "님 아직 날씨가 쌀쌀하네요.");
                    content2.setText("감기 조심하세요.");
                }
                setImage(ranMarchImgs.getResourceId(randomNumMarch, -1));
            } else if (thisMonth.equals("May")) {
                randomNumMay = checkRandomNum(randomNumMay, ranMayImgs.getIndexCount() + 1);
                if (randomNumMay == 0) {
                    content1.setText(user_name);
                    content2.setText("늦게까지 수고 많았어요.");
                }
                setImage(ranMayImgs.getResourceId(randomNumMay, -1));
            } else if (thisMonth.equals("Jul")) {
                randomNumJuly = checkRandomNum(randomNumJuly, ranJulyImgs.getIndexCount() + 1);
                setImage(ranJulyImgs.getResourceId(randomNumJuly, -1));
            } else if (thisMonth.equals("Sep")) {
                randomNumSep = checkRandomNum(randomNumSep, ranSepImgs.getIndexCount() + 1);
                if (randomNumSep == 0) {
                    content1.setText(user_name + "님 ");
                    content2.setText("걱정없는 밤 되세요.");
                }
                setImage(ranSepImgs.getResourceId(randomNumSep, -1));
            } else if (thisMonth.equals("Oct")) {
                randomNumOct = checkRandomNum(randomNumOct, ranOctImgs.getIndexCount() + 1);
                setImage(ranOctImgs.getResourceId(randomNumOct, -1));
            } else if (thisMonth.equals("Nov")) {
                randomNumNov = checkRandomNum(randomNumNov, ranNovImgs.getIndexCount() + 1);
                setImage(ranOctImgs.getResourceId(randomNumNov, -1));
            } else {
                randomNum = checkRandomNum(randomNum, ranImgs.getIndexCount() + 1);
                setImage(ranImgs.getResourceId(randomNum, -1));
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int checkRandomNum(int today_i, int arraySize) {
        if (yesterday_i == today_i) {
            today_i++;
            if (today_i > arraySize) {
                today_i = ThreadLocalRandom.current().nextInt(0, arraySize);
            }
        }
        yesterday_i = today_i;
        return today_i;
    }


    private void setImage(int resource) {
        Glide.with(this).load(resource).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(background);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_touch) {
            finish();
        }

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
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
            timeHandler = null;
            timerTask = null;
        }
    }
}

