package com.thirdtou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2018-04-16.
 */

public class ExplainLayoutAdapter extends PagerAdapter {
    private int[] image_resources = {R.drawable.explain1, R.drawable.explain2, R.drawable.explain3};
    private Context ctx;
    private LayoutInflater layoutInflater;

    public ExplainLayoutAdapter(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.activity_explain,container,false);
        Typeface typeface = Typeface.createFromAsset(ctx.getAssets(),"fonts/sdgB.ttf");
        ImageView gif_imageView = item_view.findViewById(R.id.explain1_gif);
        ImageView imageView = item_view.findViewById(R.id.explain2_image);
        ImageView closeBar = item_view.findViewById(R.id.close_imagebar);
        TextView textUpper = item_view.findViewById(R.id.upper_text);
        TextView textLower = item_view.findViewById(R.id.lower_text);
        ImageView circleLeft = item_view.findViewById(R.id.left_circle);
        ImageView circleCenter = item_view.findViewById(R.id.center_circle);
        ImageView circleRight = item_view.findViewById(R.id.right_circle);
        TextView button = item_view.findViewById(R.id.button_explain);

        if(position == 0){
            textUpper.setText("실시간 업데이트 되는 날씨정보");
            textLower.setText("매일 변하는 예쁜 잠금화면");
            textUpper.setTypeface(typeface);
            textLower.setTypeface(typeface);
            gif_imageView.setScaleY(1.9f);
            gif_imageView.setScaleX(1.9f);
            circleLeft.setImageResource(R.drawable.circlewhite);
            Glide.with(ctx).load(image_resources[position]).into(gif_imageView);
            closeBar.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            container.addView(item_view);

        }else if(position == 1){
            textUpper.setText("이름을 불러주는 앱");
            textLower.setText("생일을 축하해주는 앱");
            textUpper.setTypeface(typeface);
            textLower.setTypeface(typeface);
            imageView.setScaleX(1.01f);
            imageView.setScaleY(1.01f);
            circleCenter.setImageResource(R.drawable.circlewhite);
            Glide.with(ctx).load(image_resources[position]).into(imageView);
            closeBar.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            container.addView(item_view);

        }else if(position == 2){
            textUpper.setText("START버튼을 클릭하고");
            textLower.setText("잠금버튼을 눌렀다 켜면 실행됩니다.");
            textUpper.setTypeface(typeface);
            textLower.setTypeface(typeface);
            circleRight.setImageResource(R.drawable.circlewhite);
            imageView.setScaleX(1.01f);
            imageView.setScaleY(1.01f);
            closeBar.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            button.setTypeface(typeface);
            Glide.with(ctx).load(image_resources[position]).into(imageView);
            container.addView(item_view);

        }

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((LinearLayout)object);
    }

}
