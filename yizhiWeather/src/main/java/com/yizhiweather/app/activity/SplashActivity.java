package com.yizhiweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.yizhiweather.app.R;

/**
 * Created by Administrator on 2017/3/28.
 */

public class SplashActivity extends Activity {
    private ImageView welcomePic;
    private TextView versionInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rl_splash_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        welcomePic=(ImageView)findViewById(R.id.iv_splash_welcome);
        versionInfo=(TextView)findViewById(R.id.tv_splash_version);
        setWelcomeSplash();
    }

    private void setWelcomeSplash(){
        welcomePic.setImageResource(R.drawable.ios_weather_icon);
        try{
            PackageManager pkManager=this.getPackageManager();
            PackageInfo pkInfo=pkManager.getPackageInfo(this.getPackageName(),0);
            String version=pkInfo.versionName;
            versionInfo.setText("V "+version + "  " + "Made by Ashelyll");
        }catch(Exception e){
            e.printStackTrace();
        }
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation =
                new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2500);
        scaleAnimation.setInterpolator(this, android.R.interpolator.linear);
        scaleAnimation.setAnimationListener(new AnimationListener(){
        	@Override
        	public void onAnimationStart(Animation animation){
        		
        	}
        	
        	@Override
        	public void onAnimationRepeat(Animation animation){
        		
        	}
        	@Override
        	public void onAnimationEnd(Animation animation){
        		Intent intent=new Intent(SplashActivity.this,ChooseAreaActivity.class);
                startActivity(intent);
                finish();
        	}
        });
        
        animationSet.addAnimation(scaleAnimation);
        welcomePic.startAnimation(animationSet);
    }
}
