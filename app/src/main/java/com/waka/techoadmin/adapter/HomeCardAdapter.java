package com.waka.techoadmin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.waka.techoadmin.R;
import com.waka.techoadmin.model.Product;

import java.util.ArrayList;

public class HomeCardAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private ArrayList<Product> productList;
    Context context;
    Fragment fragment;
    private String imageUrl = "/productImages/document";

    public HomeCardAdapter(ArrayList<Product> productList, Context context, Fragment fragment) {
        this.productList = productList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View card = inflater.inflate(R.layout.home_card, parent, false);
        return new ProductViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product item = productList.get(position);
        Glide.with(context).load(item.getProductImage().get(0)).into(holder.productImage);
        holder.getName().setText(item.getName());
        holder.getCategory().setText(item.getCategory());
        holder.getPrice().setText(String.valueOf("LKR " + item.getPrice() + 0));


        // delete product image long click ----------------------------------------------------------
        holder.productImage.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        ArrayList<String> downloadImagesList = item.getProductImage();

                        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                        builder.setTitle(Html.fromHtml("<font font-size='35dp' text-alignment='center'>" + "Warning" + "</font>"));
                        builder.setIcon(R.drawable._608733_warning_icon);
                        builder.setCancelable(true);
                        builder.setMessage(Html.fromHtml("<font color='#787878'>" + "Do you want to delete this product permanently?" + "</font>"));

                        // yes button - delete product----------------------------------------------
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");

                                // delete product from database-----------------------------------------------------
                                reference.child(item.getId()).removeValue();
                                Toast.makeText(v.getContext(), "Product Deleted", Toast.LENGTH_LONG).show();

                                for (int i = 0; i < downloadImagesList.size(); i++) {

                                    StorageReference storageReference = FirebaseStorage.getInstance()
                                            .getReferenceFromUrl(downloadImagesList.get(i));

                                    // Delete the file ---------------------------------------------------------
                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
//                                            Toast.makeText(fragment.getContext(), "Image Removed"+downloadImagesList, Toast.LENGTH_LONG).show();
                                            System.out.println("Image Removed");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Toast.makeText(fragment.getContext(), "Image Removing Failed", Toast.LENGTH_LONG).show();
                                            System.out.println("Image Removing Failed");
                                        }
                                    });
                                }

                                notifyDataSetChanged();
                            }
                        });

                        // no button - cancel delete product----------------------------------------------
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog alertDialog = builder.create();
                                alertDialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        return true;
                    }
                }
        );


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void searchDataList(ArrayList<Product> searchList) {
        productList = searchList;
        notifyDataSetChanged();
    }

}
