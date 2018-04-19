package com.thirdtou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2018-04-16.
 */

public class ViewPagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    ExplainLayoutAdapter adapter;

    SharedPreference sharedPreference = new SharedPreference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_viewpager);
        viewPager = findViewById(R.id.view_pager);
        adapter = new ExplainLayoutAdapter(this);
        viewPager.setAdapter(adapter);
        TextView textView = findViewById(R.id.button_explain);
    }

    private boolean getLogin(){return(sharedPreference.getValue(this, "isLogin", false));}

    private <T> void goNext(Class<T> target){

        Intent intent = new Intent(this,target);
        startActivity(intent);
        finish();

    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_explain:
                if (getLogin()) {
                    goNext(LastActivity.class);
                } else {
                    goNext(MainActivity.class);
                }
        }

    }
}
