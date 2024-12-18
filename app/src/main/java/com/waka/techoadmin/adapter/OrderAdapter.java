package com.waka.techoadmin.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.waka.techoadmin.LoginActivity;
import com.waka.techoadmin.MainActivity;
import com.waka.techoadmin.ProfileFragment;
import com.waka.techoadmin.R;
import com.waka.techoadmin.model.Order;
import com.waka.techoadmin.model.Product;
import com.waka.techoadmin.model.User;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    //    private Activity activity;
    private Fragment fragment;
    Context context;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public OrderAdapter(Context context, ArrayList<Order> orderArrayList, Fragment fragment) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.fragment = fragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView userName, productName, qty, date;
        public Button processBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            userName = itemView.findViewById(R.id.userNameText);
            productName = itemView.findViewById(R.id.productText);
            qty = itemView.findViewById(R.id.qtyText);
            date = itemView.findViewById(R.id.dateText);
            processBtn = itemView.findViewById(R.id.imageButton6);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Order order = orderArrayList.get(position);


        if (order.getStatus().equals("Processing")) {
            holder.processBtn.setText("Processing");
            holder.processBtn.setEnabled(false);
        } else if (order.getStatus().equals("Canceled")) {
            holder.processBtn.setText("Canceled");
            holder.processBtn.setEnabled(false);
        } else if (order.getStatus().equals("Delivered")) {
            holder.processBtn.setText("Delivered");
            holder.processBtn.setEnabled(false);
        }


        holder.qty.setText(String.valueOf("Quantity : " + order.getQty()));
        holder.date.setText(order.getDate());
        holder.productName.setText(order.getProductId());


        final User[] user = new User[1]; //array of user objects

        // load user data -------------------------------------------------------------------------------
        db.collection("users").document(order.getUserId()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            System.out.println(task.getResult().getData());
                            user[0] = task.getResult().toObject(User.class);
//                            mobile[0] = user[0].getMobile();

                            holder.userName.setText(user[0].getFirstName() + " " + user[0].getLastName());
                            if (user[0].getUserDp() != null) {
                                Glide.with(context).load(user[0].getUserDp()).circleCrop().into(holder.imageView);
                            } else {
                                holder.imageView.setImageResource(R.drawable.user_non_dp);
                            }
                        }
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context.getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();
            }
        });


        // load product data ----------------------------------------------------------------------------
        final Product[] product = new Product[1];
        DatabaseReference reference = firebaseDatabase.getReference("Products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                    product[0] = data.getValue(Product.class);
                    if (data.getKey().equals(order.getProductId())) {
                        holder.productName.setText(product[0].getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(), "Connection Failed. Try Again Later", Toast.LENGTH_LONG).show();
            }
        });

        // SMS notification permission --------------------------------------------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(fragment.getActivity(), new String[]{
                    Manifest.permission.SEND_SMS
            }, 100);
        }


        // process btn ---------------------------------------------------------------------------------
        holder.processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // update order status -------------------------------------------------------------------------
                FirebaseDatabase.getInstance().getReference("Order")
                        .child(order.getOrderId())
                        .child("status").setValue("Processing")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                holder.processBtn.setText("Processing");
                                holder.processBtn.setEnabled(false);

                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(user[0].getMobile(), null, "Hi "
                                        + user[0].getFirstName() + " " + user[0].getLastName()
                                        + ",\n" + "Your " + product[0].getName() + " order is processing.\n\n"
                                        + "Thank you for choosing Techno!", null, null);

                                Toast.makeText(v.getContext(), "Order Processing", Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context.getApplicationContext(), "Order Processing Failed", Toast.LENGTH_LONG).show();
                            }
                        });
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }
}