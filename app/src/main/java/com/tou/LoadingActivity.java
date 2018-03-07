package com.tou;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * Created by user1 on 2018-02-23.
 */

public class LoadingActivity extends AppCompatActivity implements Runnable {

    ImageView tou_icon;

    SharedPreference sharedPreference= new SharedPreference();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadpage);

        tou_icon = findViewById(R.id.logo_gif);
        setImage(R.drawable.tou_icon);

        //2초간 딜레이후 실행
        Handler handler = new Handler();
        handler.postDelayed(this,5000);
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

        Toast.makeText(this,target.getName(), Toast.LENGTH_SHORT).show();
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
        if (getLogin()){
                goNext(LastActivity.class);
        } else {
                goNext(MainActivity.class);
            }

        }

}
