package com.example.foodforneedy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class DonorActivity extends AppCompatActivity {
    EditText name,mobile,district,address,food_amount,food_info;
    TextView nav_email,nav_name,don_count;
    Button donor_button;
    Uri downloadUrl;
    Uri uri;
    NavigationView nav;
    androidx.appcompat.widget.Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    ImageView food_image,nav_image;
    String uid;
    ProgressBar progressBar;
    View progressBar_back;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        donor_init();

        setting_navigation_drawer();

        animation();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(DonorActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        setting_navigation_drawer_components();

        food_image.setOnClickListener(new View.OnClickListener() {         //To select image
            @Override
            public void onClick(View v) {
                food_image.setImageResource(0);
                Intent intent=new Intent();
                intent.setType("image/*");                        //select image from mobile
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent,0);    //function to override

            }
        });

        donor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar_back.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                uploadImageToFirebaseStorage();
            }

        });

    }       //End of onCreate()

    private void updateDonationNo() {
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                String d= snapshot.child(uid).child("donationNo").getValue().toString();
                setValue(d,uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setValue(String d,String uid) {
        int dvalue=Integer.parseInt(d);
        dvalue+=1;
        String res=String.valueOf(dvalue);
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("donationNo").setValue(res);
    }

    private void uploadDonationInfoWithImageUrlToDatabase(Uri uri) {
        String donor_name=name.getText().toString();
        String donor_mobile=mobile.getText().toString();
        String donor_district=district.getText().toString();
        String donor_address=address.getText().toString();
        String donor_food_amount=food_amount.getText().toString();
        String donor_food_info=food_info.getText().toString();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        String currentDate= simpleDateFormat.format(calendar.getTime());

            HashMap<String, Object> map = new HashMap<>();                           //This function takes image url as argument from upload image to firebase storage function
            map.put("name",donor_name);
            map.put("mobile",donor_mobile);
            map.put("district",donor_district.toLowerCase());
            map.put("address",donor_address);
            map.put("foodAmount",donor_food_amount);
            map.put("foodInfo",donor_food_info);
            map.put("foodImageUrl",uri.toString());
            map.put("date",currentDate);
            map.put("timestamp", ServerValue.TIMESTAMP);

            FirebaseDatabase.getInstance().getReference().child("Donations").push().setValue(map)     //get access to firebase
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.INVISIBLE);
                            progressBar_back.setVisibility(View.INVISIBLE);
                            Toast.makeText(DonorActivity.this,"Donation Request added Successfully", Toast.LENGTH_SHORT).show();
                            updateDonationNo();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar_back.setVisibility(View.INVISIBLE);
                    Log.i("fthj","Failed reason:"+e.toString());
                    Toast.makeText(DonorActivity.this,"Failed reason:"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void uploadImageToFirebaseStorage() {
        if(!form_validation())
            return;

        String filename= UUID.randomUUID().toString();         //To give random name to image saved in database
        StorageReference ref= FirebaseStorage.getInstance().getReference("/images/"+filename);     //get access to folde in firebase storage
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {          //put file to the refernce obtained bt sending uri
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {        //on success of put file operation get url
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrl=uri;                                                 //get image url in downloadURL variable
                        Log.i("fthj","URL of Image"+uri);

                        uploadDonationInfoWithImageUrlToDatabase(uri);                 //upload complete vehicle info along with vehicle image url to database

                    }

                });

            }

        });
    }

    private boolean form_validation() {
        if(uri==null){
            progressBar_back.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(DonorActivity.this,"Please Select an Image: ",Toast.LENGTH_SHORT).show();
            return false;
        }
        String donor_name=name.getText().toString();
        String donor_mobile=mobile.getText().toString();
        String donor_district=district.getText().toString();
        String donor_address=address.getText().toString();
        String donor_food_amount=food_amount.getText().toString();
        String donor_food_info=food_info.getText().toString();

        if(donor_name.isEmpty() || donor_mobile.isEmpty() || donor_district.isEmpty() || donor_address.isEmpty() || donor_food_amount.isEmpty() ||donor_food_info.isEmpty()){
            progressBar_back.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(DonorActivity.this,"Empty strings: ",Toast.LENGTH_SHORT).show();
            return false;

        }
        else if (donor_mobile.length()!=10){
            progressBar_back.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(DonorActivity.this,"Phone no. not correct ",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setting_navigation_drawer_components() {
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
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
        toolbar= (androidx.appcompat.widget.Toolbar) findViewById(R.id.donor_toolbar);
        setSupportActionBar(toolbar);
        nav=findViewById(R.id.navmenu);
        drawerLayout=findViewById(R.id.drawer);
        nav_name=nav.getHeaderView(0).findViewById(R.id.nav_name);
        nav_email=nav.getHeaderView(0).findViewById(R.id.nav_email);
        nav_image=nav.getHeaderView(0).findViewById(R.id.nav_image);
        don_count=nav.getHeaderView(0).findViewById(R.id.donations_count);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void donor_init() {
        name=findViewById(R.id.donor_name);
        mobile=findViewById(R.id.donor_mobile);
        district=findViewById(R.id.donor_district);
        address=findViewById(R.id.donor_address);
        food_amount=findViewById(R.id.donor_food_amount);
        food_info=findViewById(R.id.donor_food_info);
        donor_button=findViewById(R.id.donor_button);
        food_image=findViewById(R.id.login_imageView);
        progressBar=findViewById(R.id.donor_progressBar);
        progressBar_back=findViewById(R.id.progressBar_layout);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {    //image select
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode== Activity.RESULT_OK  && data!=null){
            uri=data.getData();             //uri of image is obtained in uri variable
            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapDrawable bitmapDrawable= new BitmapDrawable(bitmap);
            food_image.setBackgroundDrawable(bitmapDrawable);       //set button as image

        }
    }

    private void animation() {
        Animation animimgpage, btn1, btn2, btn3, lefttoright, righttoleft;

        animimgpage = AnimationUtils.loadAnimation(this,R.anim.animimgpage);
        btn1 = AnimationUtils.loadAnimation(this,R.anim.btn1);
        btn2 = AnimationUtils.loadAnimation(this,R.anim.btn2);
        btn3 = AnimationUtils.loadAnimation(this,R.anim.btnhree);
        lefttoright = AnimationUtils.loadAnimation(this,R.anim.lefttoright);
        righttoleft = AnimationUtils.loadAnimation(this,R.anim.righttoleft);

        food_image.startAnimation(btn3);
        name.startAnimation(btn3);
        mobile.startAnimation(btn3);
        district.startAnimation(btn3);
        address.startAnimation(btn3);
        food_amount.startAnimation(btn3);
        food_info.startAnimation(btn3);
        donor_button.startAnimation(btn3);
    }

}