package com.example.foodforneedy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {
    Animation animimgpage, btn1, btn2, btnhree, lefttoright, righttoleft;
    ImageView imageView;
    TextView slogan,app_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView=findViewById(R.id.imageView);
        slogan=findViewById(R.id.splash_slogan);
        app_name=findViewById(R.id.company_name);

        animimgpage = AnimationUtils.loadAnimation(this,R.anim.animimgpage);
        btn1 = AnimationUtils.loadAnimation(this,R.anim.btn1);
        btn2 = AnimationUtils.loadAnimation(this,R.anim.btn2);
        btnhree = AnimationUtils.loadAnimation(this,R.anim.btnhree);
        lefttoright = AnimationUtils.loadAnimation(this,R.anim.lefttoright);
        righttoleft = AnimationUtils.loadAnimation(this,R.anim.righttoleft);

        app_name.startAnimation(animimgpage);
        imageView.startAnimation(btn2);
        slogan.startAnimation(btnhree);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Splash.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },8000);


    }
}