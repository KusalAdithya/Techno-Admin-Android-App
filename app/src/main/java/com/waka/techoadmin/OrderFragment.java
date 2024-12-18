package com.waka.techoadmin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.waka.techoadmin.adapter.OrderAdapter;
import com.waka.techoadmin.model.Order;

import java.util.ArrayList;
import java.util.Collections;

public class OrderFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private View emptyView, layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        emptyView = fragment.findViewById(R.id.emptyOderView);
        layout = fragment.findViewById(R.id.constraintLayoutn3);

        //Products
        ArrayList<Order> orderArrayList = new ArrayList<>();

        RecyclerView recyclerView = fragment.findViewById(R.id.orderCardView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OrderAdapter adapter =  new OrderAdapter(getContext(), orderArrayList, OrderFragment.this);;
//        try {
//            adapter = new OrderAdapter(getContext(), orderArrayList, OrderFragment.this);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        } catch (java.lang.InstantiationException e) {
//            throw new RuntimeException(e);
//        }


        // load from db ----------------------------------------------------------------------------
        OrderAdapter finalAdapter = adapter;
        firebaseDatabase.getReference("Order").orderByValue()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getChildrenCount() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                            layout.setVisibility(View.INVISIBLE);

                        } else {
                            orderArrayList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                Order oder = snapshot.getValue(Order.class);
                                orderArrayList.add(oder);
                                System.out.println(snapshot);
                            }

                            Collections.reverse(orderArrayList);
                            System.out.println("order array  - " + orderArrayList);

                            finalAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(finalAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Connection Failed. Try Again Later", Toast.LENGTH_LONG).show();
                    }
                });

    }
}