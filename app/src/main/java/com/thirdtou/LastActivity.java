package com.thirdtou;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thirdtou.utils.LockScreen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018-02-20.
 */

public class LastActivity extends AppCompatActivity implements View.OnClickListener {


    //TextView textView;
    View view;
    TextView text1, username, text2, hour, min, day, date, month, year;
    String user_name,date_time, user_birth;
    Typeface font1,font2,font3,font4,font5;
    SharedPreference sharedPreference=new SharedPreference();
    UserData userData;
    Button startButton;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);

        initView();
        setTypeFace();
        getData();
        username.setText(user_name);
        LockScreen.getInstance().init(getApplicationContext());
        checkPermission();


        //lockScreen.active();

    }

    /**
     * UserData를 디비에서 가져와서 세팅
     * userName 과 birth 데이터를 가져올 수 있음
     * 디비에 저장된 json 을 gson 을 통하여 UserData 클래스에 매핑**/
    private void getData(){
        String data = sharedPreference.getValue(this,"userData","");
        Gson gson = new Gson();
        userData = gson.fromJson(data,UserData.class);
        if (userData!=null){
            user_name = userData.getName();
            user_name = user_name.replace(" ","");
            user_birth = userData.getBirth();
        }
    }

    private void initView(){
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        hour = findViewById(R.id.hour);
        min = findViewById(R.id.minute);
        day = findViewById(R.id.day);
        date = findViewById(R.id.date);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(this);

        username = findViewById(R.id.username);

        try {
            Formatterclass();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Toast.makeText(this,"START버튼을 클릭 후 잠금 버튼을 눌렀다가 해지해주세요.",Toast.LENGTH_LONG).show();

    }

    private void checkPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
    }

    private void Formatterclass() throws ParseException {

        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMMM dd E hh mm", Locale.ENGLISH);
        date_time = formatter.format(today);

        splitCurrentTime(date_time);

    }
    private void splitCurrentTime(String string){

        String c_year, c_month, c_day, c_date, c_hour, c_min;

        String[] today = string.split(" ");

        for(int i = 0 ; i <=5 ; i++){

            if(i == 0){
                c_year = today[0];
                year.setText(c_year);
            }else if(i == 1){
                c_month = today[1];
                c_month = c_month.toUpperCase();
                month.setText(c_month);
            }else if(i == 2){
                c_date = today[2];
                date.setText(c_date);
            }else if(i == 3){
                c_day = today[3];
                c_day = c_day.toUpperCase();
                c_day = convertLetter(c_day);
                day.setText(c_day);
            }else if(i == 4){
                c_hour = today[4];
                hour.setText(c_hour);
            }else if(i == 5){
                c_min = today[5];
                min.setText(c_min);
            }

        }

    }

    private void setTypeFace() {
        font1 = Typeface.createFromAsset(getAssets(), "fonts/smr.ttf");
        font2 = Typeface.createFromAsset(getAssets(), "fonts/jejug.ttf");
        font3 = Typeface.createFromAsset(getAssets(),"fonts/nanumgEX.ttf");
        font4 = Typeface.createFromAsset(getAssets(),"fonts/nanumgL.ttf");
        font5 = Typeface.createFromAsset(getAssets(),"fonts/nanumg.ttf");

        SpannableStringBuilder SS = new SpannableStringBuilder("지금부터 TOU가");
        SS.setSpan(new CustomTypefaceSpan("", font1), 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan(new CustomTypefaceSpan("", font1), 8, 9, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan(new CustomTypefaceSpan("", font2), 5, 7, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        text1.setText(SS);
        text2.setTypeface(font1);
        username.setTypeface(font1);
        hour.setTypeface(font3);
        min.setTypeface(font3);
        month.setTypeface(font4);
        date.setTypeface(font5);
        day.setTypeface(font3);
        year.setTypeface(font4);

    }
    private String convertLetter(String str){

        if (str.equals("MON")) {
            str = "MONDAY";
        }else if(str.equals("TUE")){
            str = "TUESDAY";
        }else if(str.equals("WED")){
            str = "WEDNESDAY";
        }else if(str.equals("THU")){
            str = "THURSDAY";
        }else if(str.equals("FRI")){
            str = "FRIDAY";
        }else if(str.equals("SAT")){
            str = "SATURDAY";
        }else if(str.equals("SUN")){
            str = "SUNDAY";
        }

        return str;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_start:
                //서비스-> 알람메니저 / 렌덤함수로 알람시간 지정(24시간 주기) / notification 띄우기 /
                //날씨 데이터 업뎃 해서 / 생일 /생일 제외한 비오는날 / 비안오는날

                //Toast.makeText(this,"잠금 버튼을 눌렀다가 해지해주세요.",Toast.LENGTH_LONG).show();
                finish();

                LockScreen.getInstance().active();
                //LockScreen 활성화 시키기
                break;
        }
    }
}