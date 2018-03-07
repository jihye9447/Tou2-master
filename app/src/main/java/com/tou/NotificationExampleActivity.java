package com.tou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.tou.Weather.MinutelyWeather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
    Context context;

    TextView currentdate, celcious, content1, content2;
    Typeface typeface1, typeface2, typeface3;
    ImageView weatherIcon, icon;
    Button button_touch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_notificationexample);

        showComponet();
        setTypeface();
        setDate();
        getData();
        setIcon();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

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

        //Toast.makeText(this, latitude+"+"+longitude, Toast.LENGTH_SHORT).show();
        getWeather(latitude, longitude);

    }

    //GPS code

    public void GPSEvent() {
        if (gps == null) {
            gps = new GPSTracker(NotificationExampleActivity.this, mHandler);
        } else {
            gps.Update();
        }

        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
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
        Toast.makeText(this, str + " 현재시간: " + date2, Toast.LENGTH_SHORT).show();

    }

    //화면 초기화
    private void showComponet() {

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

    private void setDate(){
        Date date = new Date();
        SimpleDateFormat formatdate = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat formatmonth = new SimpleDateFormat("MMM",Locale.ENGLISH);
        String str1 = formatdate.format(date);
        String str2 = formatmonth.format(date).toUpperCase(Locale.ENGLISH);
        String result = str1+" "+str2;
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
        currentdate.setTypeface(typeface2);
        celcious.setTypeface(typeface2);
        content1.setTypeface(typeface1);
        content2.setTypeface(typeface1);

    }

    //화면종료 event
//    @Override
//    public void onClick(View view) {
//        if(view == button_exit){
//            Toast.makeText(this, "화면을 종료합니다.", Toast.LENGTH_SHORT).show();
//            System.exit(0);
//        }
//    }
    private void setWeatherIcon(String skycode) {

        if (skycode.equals("SKY_A01")) {
            setImage(R.drawable.sky_a01, true);
        } else if (skycode.equals("SKY_A02")) {
            setImage(R.drawable.sky_a02,false);
        } else if (skycode.equals("SKY_A03")) {
            setImage(R.drawable.sky_a03,false);
        } else if (skycode.equals("SKY_A04")) {
            setImage(R.drawable.sky_a04,false);
        } else if (skycode.equals("SKY_A05")) {
            setImage(R.drawable.sky_a05,false);
        } else if (skycode.equals("SKY_A06")) {
            setImage(R.drawable.sky_a06,false);
        } else if (skycode.equals("SKY_A07")) {
            setImage(R.drawable.sky_a01,false);
        } else if (skycode.equals("SKY_A08")) {
            setImage(R.drawable.sky_a08,false);
        } else if (skycode.equals("SKY_A09")) {
            setImage(R.drawable.sky_a09,false);
        } else if (skycode.equals("SKY_A10")) {
            setImage(R.drawable.sky_a10,false);
        } else if (skycode.equals("SKY_A11")) {
            setImage(R.drawable.sky_a11,false);
        } else if (skycode.equals("SKY_A12") || skycode.equals("SKY_A14")) {
            setImage(R.drawable.sky_a12,false);
        } else if (skycode.equals("SKY_A13")) {
            setImage(R.drawable.sky_a13,false);
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
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
                    float result = Math.round(temp1*10f)/10f;
                    temper = String.valueOf(result + "C");

                    if (object != null) {
                        //데이터가 null이 아니면 날씨 데이터 텍스트 뷰로 보여주기
                        setBackground(skyCode);
                        celcious.setText(temper);
                        setWeatherIcon(skyCode);

                    }
                }


            }

            @Override
            public void onFailure(Call<MinutelyWeather> call, Throwable t) {
                Log.d("test123", t.getLocalizedMessage() + ",");
            }
        });

    }

    private void getData() {
        String data = sharedPreference.getValue(context, "userData", "");
        Gson gson = new Gson();
        userData = gson.fromJson(data, UserData.class);
        if (userData != null) {
            user_name = userData.getName();
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
        SimpleDateFormat formatter3 = new SimpleDateFormat("MMM",Locale.ENGLISH);

        try {
            Date  date = formatter2.parse(user_birth);
            String birth = formatter.format(date);
            String[] specialDay = {startSt, birth, "24 Sep", "07 Aug"};
            Date today = new Date();
            today.setTime(System.currentTimeMillis());
            String todaySt = formatter.format(today);
            String thisMonth = formatter3.format(today);
            if (todaySt.equals(specialDay[0])){
                setImage(R.drawable.firstday);
            } else if (todaySt.equals(specialDay[1])){
                setImage(R.drawable.birthday);
            } else if(todaySt.equals(specialDay[2])){
                setImage(R.drawable.sep_24th);
            } else if (todaySt.equals(specialDay[3])){
                setImage(R.drawable.aug_7th);
            } else {
                Log.d("test123",skyCode+",,");
                setWeatherBackground(skyCode, thisMonth);
            }
        } catch (ParseException e) {
            Log.d("test123",e.getLocalizedMessage()+",,");
        }


    }


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
        int rainyNum = ThreadLocalRandom.current().nextInt(0, rainyImgs.getIndexCount() + 1);
        int randomNum = ThreadLocalRandom.current().nextInt(0, ranImgs.getIndexCount() + 1);
        int randomNumAug = ThreadLocalRandom.current().nextInt(0, ranAugImgs.getIndexCount() + 1);
        int randomNumMarch = ThreadLocalRandom.current().nextInt(0, ranMarchImgs.getIndexCount() + 1);
        int randomNumMay = ThreadLocalRandom.current().nextInt(0, ranMayImgs.getIndexCount() + 1);
        int randomNumJuly = ThreadLocalRandom.current().nextInt(0, ranJulyImgs.getIndexCount() + 1);
        int randomNumOct = ThreadLocalRandom.current().nextInt(0, ranOctImgs.getIndexCount() + 1);
        int randomNumNov = ThreadLocalRandom.current().nextInt(0, ranNovImgs.getIndexCount() + 1);

        if (skycode.equals("SKY_A04") || skycode.equals("SKY_A08")) {
            setImage(rainyImgs.getResourceId(rainyNum,-1));
        } else if (skycode.equals("SKY_A06") || skycode.equals("SKY_A10")) {
            setImage(rainyImgs.getResourceId(rainyNum,-1));
        } else if (skycode.equals("SKY_A12")) {
            //thunder, rainy
            setImage(rainyImgs.getResourceId(rainyNum,-1));
        } else if (skycode.equals("SKY_A14")) {
            setImage(rainyImgs.getResourceId(rainyNum,-1));
            //vector rainy,snowy-5,thuder
        } else  {
            //simpledatecode pasing 한게 MMM aug,march,may,july,oct,nov)
            if(thisMonth.equals("Aug")){
                setImage(ranAugImgs.getResourceId(randomNumAug,-1));
            }else if(thisMonth.equals("Mar")){
                setImage(ranMarchImgs.getResourceId(randomNumMarch,-1));
            }else if (thisMonth.equals("May")) {
                setImage(ranMayImgs.getResourceId(randomNumMay,-1));
            }else if(thisMonth.equals("Jul")){
                setImage(ranJulyImgs.getResourceId(randomNumJuly,-1));
            }else if(thisMonth.equals("Oct")){
                setImage(ranOctImgs.getResourceId(randomNumOct,-1));
            }else if(thisMonth.equals("Nov")){
                setImage(ranOctImgs.getResourceId(randomNumNov,-1));
            }else{
                setImage(ranImgs.getResourceId(randomNum,-1));
            }
        }

    }


    private void setImage(int resource) {
        Glide.with(this).load(resource).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.d("test1234",e.getMessage()+",");
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(background);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onClick(View view) {

        if(view == button_touch){
            finish();
        }

    }
}

