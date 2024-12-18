package com.waka.techoadmin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.waka.techoadmin.adapter.CategorySlideAdapter;
import com.waka.techoadmin.adapter.HomeCardAdapter;

import com.waka.techoadmin.model.Product;
import com.waka.techoadmin.model.Tag;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    ArrayList<Tag> tagArrayList;
    String[] tagsName;
    private FirebaseFirestore db;
    private FirebaseDatabase firebaseDatabase;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        db=FirebaseFirestore.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        ArrayList<Product> productArrayList = new ArrayList<>();

        searchView = fragment.findViewById(R.id.textInputSearch);
        searchView.clearFocus();


        //Category Slider --------------------------------------------------------------------------
        categoryInitialized();
        RecyclerView categoryRecycle = fragment.findViewById(R.id.categorySliderView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecycle.setLayoutManager(horizontalLayoutManager);
        categoryRecycle.setHasFixedSize(true);
        CategorySlideAdapter categorySlideAdapter = new CategorySlideAdapter(getContext(), tagArrayList);
        categoryRecycle.setAdapter(categorySlideAdapter);



        //Products View ----------------------------------------------------------------------------
        RecyclerView cardRecycle = fragment.findViewById(R.id.homeCardView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        cardRecycle.setLayoutManager(gridLayoutManager);
        HomeCardAdapter homeCardAdapter = new HomeCardAdapter(productArrayList, getContext(), HomeFragment.this);
        cardRecycle.removeAllViews();
        cardRecycle.setAdapter(homeCardAdapter);
        cardRecycle.setHasFixedSize(true);


        // load products data ----------------------------------------------------------------------
        firebaseDatabase.getReference("Products").orderByChild("dateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productArrayList.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                }
                Collections.reverse(productArrayList);
                homeCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Db data load fail", Toast.LENGTH_LONG).show();
            }
        });

        // Search ----------------------------------------------------------------------------------
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Product> searchList = new ArrayList<>();
                for(Product product:productArrayList){
                    if (product.getName().toLowerCase().contains(newText.toLowerCase())){
                        searchList.add(product);
                    }
                }
                homeCardAdapter.searchDataList(searchList);
                return true;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        });

    }

    // For Category Slider -------------------------------------------------------------------------
    private void categoryInitialized() {
        tagArrayList = new ArrayList<>();

        tagsName = new String[]{
                "All",
                "SmartPhones",
                "Laptops",
                "Computers",
                "Smart Watches",
        };

        for (String s : tagsName) {

            Tag tags = new Tag(s);
            tagArrayList.add(tags);
        }


    }
}