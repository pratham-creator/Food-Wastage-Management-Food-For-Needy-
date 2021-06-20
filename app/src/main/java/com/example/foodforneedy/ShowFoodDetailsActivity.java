package com.example.foodforneedy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ShowFoodDetailsActivity extends AppCompatActivity {
    TextView info_text_view,address_text_view,amount_text_view,mobile_text_view,name_text_view;
    ImageView food_imageView;
    String info,image,address,amount,name,mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_food_details);

        show_details_init();

        setting_components();

        animation();

    }   //End of onCreate()

    private void setting_components() {
        info_text_view.setText(info);
        amount_text_view.setText(amount);
        name_text_view.setText(name);
        mobile_text_view.setText(mobile);
        address_text_view.setText(address);

        Glide.with(this).load(image).into(food_imageView);
    }

    private void show_details_init() {
        info_text_view=findViewById(R.id.food_details_food_info);
        amount_text_view=findViewById(R.id.no_of_people);
        address_text_view=findViewById(R.id.address);
        mobile_text_view=findViewById(R.id.mobile);
        name_text_view=findViewById(R.id.name);
        food_imageView=findViewById(R.id.show_details_imageView);

        info=getIntent().getStringExtra("foodInfo");
        image=getIntent().getStringExtra("foodImage");
        address=getIntent().getStringExtra("foodAddress");
        name=getIntent().getStringExtra("foodName");
        mobile=getIntent().getStringExtra("foodMobile");
        amount=getIntent().getStringExtra("foodAmount");
    }

    private void animation() {
        Animation animimgpage, btn1, btn2, btn3, lefttoright, righttoleft;

        animimgpage = AnimationUtils.loadAnimation(this,R.anim.animimgpage);
        btn1 = AnimationUtils.loadAnimation(this,R.anim.btn1);
        btn2 = AnimationUtils.loadAnimation(this,R.anim.btn2);
        btn3 = AnimationUtils.loadAnimation(this,R.anim.btnhree);
        lefttoright = AnimationUtils.loadAnimation(this,R.anim.lefttoright);
        righttoleft = AnimationUtils.loadAnimation(this,R.anim.righttoleft);

        food_imageView.startAnimation(animimgpage);
        info_text_view.startAnimation(btn2);


    }
}