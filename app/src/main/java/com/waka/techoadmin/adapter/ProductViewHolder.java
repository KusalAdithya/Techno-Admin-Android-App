package com.waka.techoadmin.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waka.techoadmin.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    public ImageView productImage;
    private TextView name, category, price;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.productImage);
        name = itemView.findViewById(R.id.productNameText);
        category = itemView.findViewById(R.id.categoryText);
        price = itemView.findViewById(R.id.priceText);
    }

    public ImageView getProductImage() {
        return productImage;
    }

    public void setProductImage(ImageView productImage) {
        this.productImage = productImage;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getCategory() {
        return category;
    }

    public void setCategory(TextView category) {
        this.category = category;
    }

    public TextView getPrice() {
        return price;
    }

    public void setPrice(TextView price) {
        this.price = price;
    }
}
