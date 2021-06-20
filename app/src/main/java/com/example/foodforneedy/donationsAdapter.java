package com.example.foodforneedy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import static androidx.core.content.ContextCompat.startActivity;

public class donationsAdapter extends FirebaseRecyclerAdapter<Donations, donationsAdapter.donationsViewHolder> {
    public donationsAdapter(@NonNull FirebaseRecyclerOptions<Donations> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull donationsViewHolder holder,final int position, @NonNull Donations model) {
        holder.name.setText(model.getName());
        holder.address.setText(model.getAddress());
        holder.mobile.setText(model.getMobile());
        holder.foodAmount.setText(model.getFoodAmount());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(),ShowFoodDetailsActivity.class);
                intent.putExtra("foodInfo",model.getFoodInfo());
                intent.putExtra("foodAmount",model.getFoodAmount());
                intent.putExtra("foodAddress",model.getAddress());
                intent.putExtra("foodMobile",model.getMobile());
                intent.putExtra("foodName",model.getName());
                intent.putExtra("foodImage",model.getFoodImageUrl());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Are you sure");
                builder.setMessage("Accepted request can't be reverted");

                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String url=FirebaseDatabase.getInstance().getReference().child("Donations").child(getRef(position).getKey()).child("foodImageUrl");
                        updateDonationNo();
                        FirebaseDatabase.getInstance().getReference().child("Donations").child(getRef(position).getKey()).removeValue();
                        Toast.makeText(holder.name.getContext(),"Donation accepted Successfully",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.name.getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }

            private void updateDonationNo() {
                FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String d= snapshot.child(uid).child("donationNo").getValue().toString();
                        setValue(d,uid);
                    }

                    private void setValue(String d, String uid) {
                        int dvalue=Integer.parseInt(d);
                        dvalue+=1;
                        String res=String.valueOf(dvalue);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("donationNo").setValue(res);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }
        });



    }

    @NonNull
    @Override
    public donationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_donations, parent, false);

        return new donationsViewHolder(view);
    }

    class donationsViewHolder extends RecyclerView.ViewHolder{

        TextView name,mobile,address,foodAmount;
        Button accept;

        public donationsViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.donations_res_name);
            mobile=itemView.findViewById(R.id.donations_res_mobile);
            address=itemView.findViewById(R.id.donations_res_address);
            foodAmount=itemView.findViewById(R.id.donations_res_food_amount);
            accept=itemView.findViewById(R.id.donations_accept_request);

        }
    }
}
