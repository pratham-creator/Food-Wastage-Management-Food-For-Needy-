package com.example.foodforneedy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity {
    private String district;
    private EditText dis_box;
    private Button search;
    TextView nav_email,nav_name,don_count,don_text_view;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    ImageView nav_image;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_init();

        //String dis=getIntent().getStringExtra("dist");
        //Log.i("fthj","District of User is "+dis);

        setting_navigation_drawer();

        animation();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(SearchActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        setting_navigation_drawer_components();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_button_on_click();
            }
        });

    }       //End of onCreate()

    private void search_button_on_click() {
        district=dis_box.getText().toString();
        if(district.isEmpty()){
            Toast.makeText(SearchActivity.this,"Please Enter District ",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            Intent intent=new Intent(SearchActivity.this,ReceiverActivity.class);
            intent.putExtra("district",district.toLowerCase());
            startActivity(intent);
        }
    }

    private void setting_navigation_drawer_components() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user_name=snapshot.child(uid).child("ngo_name").getValue().toString();
                String user_email=snapshot.child(uid).child("email").getValue().toString();
                String user_donation_count=snapshot.child(uid).child("donationNo").getValue().toString();
                String user_image=snapshot.child(uid).child("profileImageUrl").getValue().toString();

                nav_name.setText(user_name);
                nav_email.setText(user_email);
                don_count.setText(user_donation_count);
                Glide.with(nav_image.getContext()).load(user_image).diskCacheStrategy(DiskCacheStrategy.ALL).into(nav_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setting_navigation_drawer() {
        androidx.appcompat.widget.Toolbar toolbar= (androidx.appcompat.widget.Toolbar) findViewById(R.id.donor_toolbar);
        setSupportActionBar(toolbar);
        nav=findViewById(R.id.receiver_navmenu);
        drawerLayout=findViewById(R.id.receiver_drawer);
        nav_name=nav.getHeaderView(0).findViewById(R.id.nav_name);
        nav_email=nav.getHeaderView(0).findViewById(R.id.nav_email);
        nav_image=nav.getHeaderView(0).findViewById(R.id.nav_image);
        don_count=nav.getHeaderView(0).findViewById(R.id.donations_count);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void search_init() {
        don_text_view=findViewById(R.id.search_donations_text_view);
        dis_box=findViewById(R.id.search_district);
        search=findViewById(R.id.search_button);
    }

    private void animation() {
        Animation animimgpage, btn1, btn2, btn3, lefttoright, righttoleft;

        animimgpage = AnimationUtils.loadAnimation(this,R.anim.animimgpage);
        btn1 = AnimationUtils.loadAnimation(this,R.anim.btn1);
        btn2 = AnimationUtils.loadAnimation(this,R.anim.btn2);
        btn3 = AnimationUtils.loadAnimation(this,R.anim.btnhree);
        lefttoright = AnimationUtils.loadAnimation(this,R.anim.lefttoright);
        righttoleft = AnimationUtils.loadAnimation(this,R.anim.righttoleft);

        don_text_view.startAnimation(lefttoright);
        dis_box.startAnimation(lefttoright);
        search.startAnimation(lefttoright);

    }
}