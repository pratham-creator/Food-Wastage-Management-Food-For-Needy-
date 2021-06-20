package com.example.foodforneedy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    EditText email,password,mobile,ngo_name,district;
    Button register_button;
    Uri downloadUrl;
    Uri uri;
    ImageView user_image;
    TextView already_have_account;
    Spinner spinner;
    String userType;
    ProgressBar progressBar;
    View progress_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_init();

        spinner_adapter();

        user_image.setOnClickListener(new View.OnClickListener() {         //To select image
            @Override
            public void onClick(View v) {
                selecting_image();
            }
        });

        already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImageToFirebaseStorage();

            }

        });


    }             //End of OnCreate

    private void uploadImageToFirebaseStorage() {
        progress_view.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        if(!is_register_form_filled())
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

                        uploadUserInfoWithImageUrlToDatabase(uri);                 //upload complete vehicle info along with vehicle image url to database

                    }

                });
            }
        });
    }

    private void uploadUserInfoWithImageUrlToDatabase(Uri uri) {
        String register_email=email.getText().toString();
        String register_password=password.getText().toString();
        String register_mobile=mobile.getText().toString();
        String register_ngo_name=ngo_name.getText().toString();
        String register_district=district.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(register_email,register_password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user=new User(register_email,userType,register_mobile,register_ngo_name,register_district,uri.toString(),"0");
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    progress_view.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this,"Register Successfull: ",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    progress_view.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this,"Register Database Failed: ",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress_view.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Failed: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean is_register_form_filled() {
        if(uri==null){
            progressBar.setVisibility(View.INVISIBLE);
            progress_view.setVisibility(View.INVISIBLE);
            Toast.makeText(RegisterActivity.this,"Please Select an Image: ",Toast.LENGTH_SHORT).show();
            return false;
        }
        String register_email=email.getText().toString();
        String register_password=password.getText().toString();
        String register_mobile=mobile.getText().toString();
        String register_ngo_name=ngo_name.getText().toString();
        String register_district=district.getText().toString();
        if(register_email.isEmpty() || register_password.isEmpty() || register_mobile.isEmpty() || register_ngo_name.isEmpty() || register_district.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            progress_view.setVisibility(View.INVISIBLE);
            Toast.makeText(RegisterActivity.this,"Empty strings: ",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(register_mobile.length()!=10){
            progressBar.setVisibility(View.INVISIBLE);
            progress_view.setVisibility(View.INVISIBLE);
            Toast.makeText(RegisterActivity.this,"Phone no. not correct ",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void selecting_image() {
        user_image.setImageResource(0);
        Intent intent=new Intent();
        intent.setType("image/*");                        //select image from mobile
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,0);    //function to override
    }

    private void spinner_adapter() {
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void register_init() {
        email=findViewById(R.id.register_email);
        password=findViewById(R.id.register_password);
        register_button=findViewById(R.id.register_button);
        already_have_account=findViewById(R.id.already_have_account);
        spinner=findViewById(R.id.register_spinner);
        ngo_name=findViewById(R.id.donor_name);
        mobile=findViewById(R.id.donor_mobile);
        district=findViewById(R.id.donor_district);
        user_image=findViewById(R.id.login_imageView);
        progressBar = findViewById(R.id.progressBar);
        progress_view=findViewById(R.id.reg_progressBar_layout);
        progress_view.setVisibility(View.INVISIBLE);
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
            user_image.setBackgroundDrawable(bitmapDrawable);       //set button as image

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        userType=parent.getItemAtPosition(position).toString();
        Log.i("fthj","Spinner selected"+userType);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}