package com.thirdtou;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thirdtou.utils.VersionChecker;

/**
 * Created by user1 on 2018-02-23.
 */

public class LoadingActivity extends AppCompatActivity implements Runnable,VersionCheckCallback {

    ImageView tou_icon;
    Typeface  typeface1;
    TextView logo_text;

    SharedPreference sharedPreference= new SharedPreference();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadpage);

        tou_icon = findViewById(R.id.logo_gif);
        logo_text = findViewById(R.id.logo);
        setImage(R.drawable.tou_icon);
        typeface1 = Typeface.createFromAsset(getAssets(), "fonts/sdgB.ttf");
        logo_text.setTypeface(typeface1);


        //2초간 딜레이후 실행
        Handler handler = new Handler();
        handler.postDelayed(this,800);
    }
    /**
     * 디비에 저장된 로그인 했는지 아닌지 판별여부 데이터 가져오는 메소드
     * @return 저장여부 boolean 형태로 return
     * */
    private boolean getLogin(){return(sharedPreference.getValue(this, "isLogin", false));}
    /**
     * 디비에 저장된 로그인 했는지 아닌지 판별여부 데이터 가져오는 메소드
     * @param  target  이동할 액티비티 class형태로 받아서 넘기는 메소드
     * @see <a href="http://palpit.tistory.com/667">http://palpit.tistory.com/667</a>
     * */
    private <T> void goNext(Class<T> target){

        Intent intent = new Intent(this,target);
        startActivity(intent);
        finish();
    }

    private void setImage(int resource){
        Glide.with(this).load(resource).into(tou_icon);
    }

    /**
     * **/
    @Override
    public void run() {

        checkVersion();

    }

    @Override
    public void getResult(boolean update) {
        if (update) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);

            //알림창 속성 설정
            builder.setMessage("새로운 업데이트가 있습니다.\n 업데이트 하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                        //확인 버튼 클릭시 설정
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final String appPackageName = getPackageName();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        //취소 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }

                    });

            final AlertDialog alertDialog = builder.create();//알림창 객체 생성
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#555555"));
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#64a2f6"));
                }
            });
            alertDialog.show();
        } else {
            if (getLogin()) {
                goNext(ViewPagerActivity.class);
            } else {
                goNext(ViewPagerActivity.class);
            }
        }
    }

    public void checkVersion(){
        VersionChecker versionChecker = new VersionChecker(getPackageManager(),getPackageName(),this);
        versionChecker.execute(getPackageName());
    }
}

