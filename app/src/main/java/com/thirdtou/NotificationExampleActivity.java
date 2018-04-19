package com.thirdtou;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.thirdtou.Weather.MinutelyWeather;
import com.thirdtou.utils.LockscreenService;
import com.thirdtou.utils.RandomNumber;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018-02-27.
 */

public class NotificationExampleActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection, LocationListener {


    //loading 화면
    ImageView loadingImage;
    TextView loading_txt;

    //randomNum
    RandomNumber randomNumber = new RandomNumber();

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
    int randomNumDec;

    LockscreenService service;


    String[] rainyImgs;
    String[] ranImgs;
    String[] ranAugImgs;
    String[] ranMarchImgs;
    String[] ranMayImgs;
    String[] ranJulyImgs;
    String[] ranOctImgs;
    String[] ranNovImgs;
    String[] ranSepImgs;
    String[] ranDecImgs;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_notificationexample);

        showComponet();
        LockApplication.activities.add(this);
        requestLocation();

    }

    private void getLocation() {

        double latitude = service.getLatitude();
        double longitude = service.getLongitude();

        if (latitude != 0 && longitude != 0) {
            getWeather(latitude, longitude);
        } else {
            requestLocation();
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
        timer.schedule(timerTask, 0, 5 * 1000);

    }

    private void requestLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            Log.d("event123","request Location");
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
            }
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, this);
            }
        }

    }

    private void updateTimeText() {
        currentTime.setText(curTime);
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

        loadingImage = findViewById(R.id.logo_gif);
        loading_txt = findViewById(R.id.logo);

        weatherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocation();
            }
        });

        button_touch.setOnClickListener(this);
        if (this.isFinishing()) {
            return;
        } else {
            Glide.with(this).load(R.drawable.noti_gradiant).into(background);
            Glide.with(this).load(R.drawable.tou_icon).into(loadingImage);
        }
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
        Log.d("event123", skycode);
        if (skycode.equals("SKY_A01")) {
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
        if (this.isFinishing()) {
            return;
        } else {
            loadingImage.setVisibility(View.GONE);
            loading_txt.setVisibility(View.GONE);
            Glide.with(this).load(resource).into(weatherIcon);
        }

    }

    @Override
    public void onAttachedToWindow() {

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onAttachedToWindow();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((LockApplication) getApplication()).lockScreenShow = true;
        bindService(new Intent(this, LockscreenService.class), this, BIND_AUTO_CREATE);

    }


    @Override
    protected void onPause() {
        super.onPause();
        ((LockApplication) getApplication()).lockScreenShow = false;
    }

    //날씨 정보 받아오는 코드
    private void getWeather(double latitude, double longtitude) {
        Log.d("event123", "long" + longtitude + "lat" + latitude);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiService.BASEURL).client(client).build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<MinutelyWeather> call = apiService.getMinutely(ApiService.APPKEY, 1, latitude, longtitude);
        call.enqueue(new Callback<MinutelyWeather>() {

            @Override
            public void onResponse(Call<MinutelyWeather> call, Response<MinutelyWeather> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        //날씨데이터를 받아옴
                        MinutelyWeather object = response.body();
                        String skyCode = object.getWeather().getMinutely().get(0).getSky().getCode();
                        String temper = object.getWeather().getMinutely().get(0).getTemperature().getTc();

                        //온도가 null 로오네요
                        float temp1 = 0f;
                        if (!TextUtils.isEmpty(temper)) {
                            temp1 = Float.parseFloat(temper);
                            float result = Math.round(temp1 * 10f) / 10f;
                            temper = String.valueOf(result + "C");
                            celcious.setText(temper);
                        } else {
                            //온도가 null 일때 세팅해주세요
                            celcious.setText("온도 받기 실패");
                        }

                        //데이터가 null이 아니면 날씨 데이터 텍스트 뷰로 보여주기
                        //setCurrent_time();
                        setBackground(skyCode);

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
            String[] specialDay = {startSt, birth, "24 Sep", "07 Aug", "25 Dec"};
            Date today = new Date();
            today.setTime(System.currentTimeMillis());
            String todaySt = formatter.format(today);
            String thisMonth = formatter3.format(today);

            Resources res = getResources();
            String[] specialImgs = res.getStringArray(R.array.specialday);

            if (todaySt.equals(specialDay[0])) {
                if (this.isFinishing()) {
                    return;
                } else {
                    Glide.with(this).load(specialImgs[0]).into(background);
                }
            } else if (todaySt.equals(specialDay[1])) {
                if (this.isFinishing()) {
                    return;
                } else {
                    Glide.with(this).load(specialImgs[1]).into(background);
                }
            } else if (todaySt.equals(specialDay[2])) {
                if (this.isFinishing()) {
                    return;
                } else {
                    Glide.with(this).load(specialImgs[2]).into(background);
                }
            } else if (todaySt.equals(specialDay[3])) {
                if (this.isFinishing()) {
                    return;
                } else {
                    Glide.with(this).load(specialImgs[3]).into(background);
                }
            } else if (todaySt.equals(specialDay[4])) {
                if (this.isFinishing()) {
                    return;
                } else {
                    Glide.with(this).load(specialImgs[4]).into(background);
                }
            } else {
                setWeatherBackground(skyCode, thisMonth, today);
            }

        } catch (ParseException e) {
        }
    }

    private void setWeatherBackground(String skycode, String thisMonth, Date date) {

        Date checkdate = service.getDate();

        //비올때 아이콘 랜덤하게 배경 출력
        Resources res = getResources();
        rainyImgs = res.getStringArray(R.array.rainIcon);
        ranImgs = res.getStringArray(R.array.randomBack);
        ranAugImgs = res.getStringArray(R.array.randomAug);
        ranMarchImgs = res.getStringArray(R.array.randomMarch);
        ranMayImgs = res.getStringArray(R.array.randomMay);
        ranJulyImgs = res.getStringArray(R.array.randomJuly);
        ranOctImgs = res.getStringArray(R.array.randomOct);
        ranNovImgs = res.getStringArray(R.array.randomNov);
        ranSepImgs = res.getStringArray(R.array.randomSep);
        ranDecImgs = res.getStringArray(R.array.randomDec);

        if (checkdate == null) {
            service.setDate(date);
            generateRandomNum();
        } else {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(checkdate);
            int checkDay = calendar.get(Calendar.DAY_OF_MONTH);
            Log.d("event123", "checkdate:" + String.valueOf(checkDay));
            calendar.setTime(date);
            int now = calendar.get(Calendar.DAY_OF_MONTH);
            Log.d("event123", "today:" + String.valueOf(now));

            if (checkDay != now) {
                service.setDate(date);
                nextNumber();
                Log.d("event123", "randomNumber: " + randomNum);
            } else {
                getRandomNumber();
            }

        }
        setImage(skycode, thisMonth);
    }

    private void setImage(String skycode, String thisMonth) {

        if (skycode.equals("SKY_A04") || skycode.equals("SKY_A08") || skycode.equals("SKY_A06") ||
                skycode.equals("SKY_A10") || skycode.equals("SKY_A12") || skycode.equals("SKY_A14")) {
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
            } else if (thisMonth.equals("Dec")) {

                setImage(ranDecImgs[randomNumDec]);
            } else {
                if (randomNum == 0) {
                    content1.setText(user_name + "님");
                    content2.setText("멋진 하루 보내세요.");
                } else if (randomNum == 1) {
                    content1.setText(user_name + "님 충분히 잘 하고 있어요.");
                    content2.setText("오늘도 화이팅.");
                } else if (randomNum == 2) {
                    content1.setText(user_name + "님");
                    content2.setText("이루고 싶은 무언가는 찾으셨나요");
                } else if (randomNum == 3) {
                    content1.setText(user_name + "님");
                    content2.setText("웃는 하루 되세요.");
                } else if (randomNum == 4) {
                    content1.setText(user_name + "님의");
                    content2.setText("멋진 날을 응원할게요.");
                }

                setImage(ranImgs[randomNum]);
            }
        }

    }

    private void nextNumber() {
        getRandomNumber();

        rainyNum++;
        randomNum++;
        randomNumAug++;
        randomNumMarch++;
        randomNumMay++;
        randomNumJuly++;
        randomNumOct++;
        randomNumNov++;
        randomNumSep++;
        randomNumDec++;

        if (rainyImgs.length == rainyNum) {
            rainyNum = 0;
        }

        if (ranImgs.length == randomNum) {
            randomNum = 0;
        }

        if (ranAugImgs.length == randomNumAug) {
            randomNumAug = 0;
        }

        if (ranMarchImgs.length == randomNumMarch) {
            randomNumMarch = 0;
        }

        if (ranMayImgs.length == randomNumMay) {
            randomNumMay = 0;
        }

        if (ranJulyImgs.length == randomNumJuly) {
            randomNumJuly = 0;
        }

        if (ranOctImgs.length == randomNumOct) {
            randomNumOct = 0;
        }
        if (ranNovImgs.length == randomNumNov) {
            randomNumNov = 0;
        }
        if (ranSepImgs.length == randomNumSep) {
            randomNumSep = 0;
        }
        if (ranDecImgs.length == randomNumDec) {
            randomNumDec = 0;
        }

        randomNumber.setRainyNum(rainyNum);
        randomNumber.setRandomNum(randomNum);
        randomNumber.setRandomNumAug(randomNumAug);
        randomNumber.setRandomNumMarch(randomNumMarch);
        randomNumber.setRandomNumMay(randomNumMay);
        randomNumber.setRandomNumJuly(randomNumJuly);
        randomNumber.setRandomNumOct(randomNumOct);
        randomNumber.setRandomNumNov(randomNumNov);
        randomNumber.setRandomNumSep(randomNumSep);
        randomNumber.setRandomNumDec(randomNumDec);

        service.setRandomNumber(randomNumber);

    }

    private void generateRandomNum() {

        Random random = new Random();
        rainyNum = random.nextInt(rainyImgs.length - 1);
        randomNum = random.nextInt(ranImgs.length - 1);
        randomNumAug = random.nextInt(ranAugImgs.length - 1);
        randomNumMarch = random.nextInt(ranMarchImgs.length - 1);
        randomNumMay = random.nextInt(ranMayImgs.length - 1);
        randomNumJuly = random.nextInt(ranJulyImgs.length - 1);
        randomNumOct = random.nextInt(ranOctImgs.length - 1);
        randomNumNov = random.nextInt(ranNovImgs.length - 1);
        randomNumSep = random.nextInt(ranNovImgs.length - 1);
        randomNumDec = random.nextInt(ranDecImgs.length - 1);
        rainyNum = checkRandomNum(rainyNum, rainyImgs.length - 1);
        randomNumAug = checkRandomNum(randomNumAug, ranAugImgs.length - 1);
        randomNumMarch = checkRandomNum(randomNumMarch, ranMarchImgs.length - 1);
        randomNumMay = checkRandomNum(randomNumMay, ranMayImgs.length - 1);
        randomNumJuly = checkRandomNum(randomNumJuly, ranJulyImgs.length - 1);
        randomNumSep = checkRandomNum(randomNumSep, ranSepImgs.length - 1);
        randomNumOct = checkRandomNum(randomNumOct, ranOctImgs.length - 1);
        randomNumNov = checkRandomNum(randomNumNov, ranNovImgs.length - 1);
        randomNumDec = checkRandomNum(randomNumDec, ranDecImgs.length - 1);
        randomNum = checkRandomNum(randomNum, ranImgs.length - 1);

        randomNumber.setRainyNum(rainyNum);
        randomNumber.setRandomNum(randomNum);
        randomNumber.setRandomNumAug(randomNumAug);
        randomNumber.setRandomNumMarch(randomNumMarch);
        randomNumber.setRandomNumMay(randomNumMay);
        randomNumber.setRandomNumJuly(randomNumJuly);
        randomNumber.setRandomNumOct(randomNumOct);
        randomNumber.setRandomNumNov(randomNumNov);
        randomNumber.setRandomNumSep(randomNumSep);
        randomNumber.setRandomNumDec(randomNumDec);

        service.setRandomNumber(randomNumber);
    }

    private void getRandomNumber() {

        randomNumber = service.getRandomNumber();
        rainyNum = randomNumber.getRainyNum();
        randomNum = randomNumber.getRandomNum();
        randomNumAug = randomNumber.getRandomNumAug();
        randomNumMarch = randomNumber.getRandomNumMarch();
        randomNumMay = randomNumber.getRandomNumMay();
        randomNumJuly = randomNumber.getRandomNumJuly();
        randomNumOct = randomNumber.getRandomNumOct();
        randomNumNov = randomNumber.getRandomNumNov();
        randomNumSep = randomNumber.getRandomNumSep();
        randomNumDec = randomNumber.getRandomNumDec();

    }


    private int checkRandomNum(int today_i, int arraySize) {
        Random random = new Random();
        if (yesterday_i == today_i) {
            today_i++;
            if (today_i > arraySize) {
                today_i = random.nextInt(arraySize - 1);
            }
        }
        yesterday_i = today_i;
        return today_i;
    }


    private void setImage(String resource) {

        if (this.isFinishing()) {
            return;
        } else {
            Glide.with(this).load(resource).into(background);
        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_touch) {
            finish();
        }

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (iBinder instanceof LockscreenService.NotiBinder) {
            LockscreenService.NotiBinder notiBinder = (LockscreenService.NotiBinder) iBinder;
            service = notiBinder.getService();

            getData();
            setTypeface();
            setDate();
            timeHandler = new TimeHandler(this);

            startRealTimeTimer();
            setIcon();
            getLocation();

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d("event123", "long: " + latitude);
        sharedPreference.putLocation(this, "lat", latitude);
        sharedPreference.putLocation(this, "long", longitude);
        getWeather(latitude, longitude);
        locationManager.removeUpdates(this);


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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timeHandler = null;
            timerTask = null;
        }
    }
}