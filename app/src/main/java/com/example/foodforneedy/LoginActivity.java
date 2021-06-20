package com.example.foodforneedy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.QueryParams;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button login_button;
    TextView create_account,reset_password;
    ProgressBar progressBar;
    View log_progress_view;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_init();

        animation();

        delete_old_donations();

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_password(v);
            }
        });

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login_button_click();

            }
        });

    }       //End of onCreate()


    private void login_button_click() {
        log_progress_view.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        String login_email=email.getText().toString();
        String login_password=password.getText().toString();
        if(login_email.isEmpty() || login_password.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            log_progress_view.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,"Empty strings: ",Toast.LENGTH_SHORT).show();

        }
        else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(login_email,login_password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            log_progress_view.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userType").getValue(String.class).equals("Donor")){
                                        Intent intent=new Intent(LoginActivity.this,DonorActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        String dist=snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("district").getValue(String.class);
                                        Intent intent=new Intent(LoginActivity.this,SearchActivity.class);
                                        intent.putExtra("dist",dist);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    log_progress_view.setVisibility(View.INVISIBLE);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            log_progress_view.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this,"Failed: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void change_password(View v) {
        EditText resetMail=new EditText(v.getContext());
        AlertDialog.Builder passResetDialog=new AlertDialog.Builder(v.getContext());
        passResetDialog.setTitle("Want to reset password?");
        passResetDialog.setMessage("Enter your email to recieve reset link");
        passResetDialog.setView(resetMail);

        passResetDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail=resetMail.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this,"Reset Link Sent Successfully!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,"Error,Reset Link not Sent!"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passResetDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this,"Cancelled",Toast.LENGTH_SHORT).show();
            }
        });

        passResetDialog.create().show();
    }

    private void delete_old_donations() {
        long cutoff=new Date().getTime()- TimeUnit.MILLISECONDS.convert(1,TimeUnit.DAYS);
        Query oldItems=FirebaseDatabase.getInstance().getReference().child("Donations").orderByChild("timestamp").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    FirebaseStorage.getInstance().getReferenceFromUrl(itemSnapshot.child("foodImageUrl").getValue().toString()).delete();
                    itemSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    private void login_init() {
        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        login_button=findViewById(R.id.login_button);
        imageView=findViewById(R.id.login_imageView);
        create_account=findViewById(R.id.create_account);
        reset_password=findViewById(R.id.forgot_password);
        progressBar=findViewById(R.id.log_progressBar);
        log_progress_view=findViewById(R.id.log_progressBar_layout);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void animation() {
        Animation animimgpage, btn1, btn2, btn3, lefttoright, righttoleft;

        animimgpage = AnimationUtils.loadAnimation(this,R.anim.animimgpage);
        btn1 = AnimationUtils.loadAnimation(this,R.anim.btn1);
        btn2 = AnimationUtils.loadAnimation(this,R.anim.btn2);
        btn3 = AnimationUtils.loadAnimation(this,R.anim.btnhree);
        lefttoright = AnimationUtils.loadAnimation(this,R.anim.lefttoright);
        righttoleft = AnimationUtils.loadAnimation(this,R.anim.righttoleft);

        imageView.startAnimation(animimgpage);
        email.startAnimation(btn2);
        password.startAnimation(btn2);
        login_button.startAnimation(btn3);
        reset_password.startAnimation(lefttoright);
        create_account.startAnimation(righttoleft);
    }
}