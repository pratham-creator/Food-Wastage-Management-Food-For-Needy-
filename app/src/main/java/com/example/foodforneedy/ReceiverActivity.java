package com.example.foodforneedy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReceiverActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    donationsAdapter adapter;
    String district;
    TextView no_donations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        receiver_init();

        district=getIntent().getStringExtra("district");

        /*
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    district = snapshot.child("district").getValue(String.class);
                    Log.i("fthj", "district *** " + district);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */

        FirebaseRecyclerOptions<Donations> options =
                new FirebaseRecyclerOptions.Builder<Donations>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Donations").orderByChild("district").equalTo(district), Donations.class)
                        .build();

        adapter = new donationsAdapter(options);

        recyclerView.setAdapter(adapter);
        Log.i("fthj","district  "+district);

        count_donations();

    }

    private void count_donations() {
        FirebaseDatabase.getInstance().getReference().child("Donations").addListenerForSingleValueEvent(new ValueEventListener() {     //To find count of items in recycler view
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for(DataSnapshot s: snapshot.getChildren()){
                    if(s.child("district").getValue(String.class).equals(district)){
                        i++;
                    }
                }
                if(i==0){
                    no_donations.setVisibility(View.VISIBLE);
                    Toast.makeText(ReceiverActivity.this,"No Request from district "+district,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void receiver_init() {
        no_donations=findViewById(R.id.no_donations);
        recyclerView=findViewById(R.id.receiver_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart () {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop () {
        super.onStop();
        adapter.stopListening();
    }

}