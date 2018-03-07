package com.tou;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tou.utils.LockScreen;

/**
 * Created by Administrator on 2018-02-15.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText input_name;
    TextView title,question;
    Button btn_r;
    Button btn_n;
    Typeface typeface1, typeface2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LockApplication.activities.add(this);
        showComponet();
        setTypeface();

        //xml 이름 입력하는 창과 연결.
        //input_name = findViewById(R.id.input_name);
        //findViewById(R.id.input_name).setOnClickListener(onButtonClick);

    }

    private void showComponet() {

        question = findViewById(R.id.name1);
        input_name = findViewById(R.id.input_name);
        btn_n = findViewById(R.id.button_next);
        btn_r = findViewById(R.id.button_reset);
        title = findViewById(R.id.logo_design);

        btn_n.setOnClickListener(this);
        btn_r.setOnClickListener(this);

    }

    public void setTypeface(){
        typeface1 = Typeface.createFromAsset(getAssets(), "fonts/smr.ttf");
        typeface2 = Typeface.createFromAsset(getAssets(),"fonts/jejug.ttf");

        btn_n.setTypeface(typeface2);
        btn_r.setTypeface(typeface2);
        input_name.setTypeface(typeface2);
        title.setTypeface(typeface2);
        question.setTypeface(typeface1);
        question.setShadowLayer(10,0,0, Color.WHITE);
    }

    @Override
    public void onClick(View view) {
        if(view==btn_n){

            String st = input_name.getText().toString();
            if(!st.equals("")){
            Toast.makeText(getBaseContext(),st,Toast.LENGTH_LONG).show();
            btn_n.setShadowLayer(10,0,0, Color.WHITE);
            Intent intent = new Intent(getApplicationContext(),ChecknameActivity.class);
            intent.putExtra("username",st);
            startActivity(intent);}

        }else if(view==btn_r){

            btn_r.setShadowLayer(10,0,0, Color.WHITE);
            input_name.setText("");
            Toast.makeText(getBaseContext(),"reset button clicked",Toast.LENGTH_LONG).show();

        }

    }

}