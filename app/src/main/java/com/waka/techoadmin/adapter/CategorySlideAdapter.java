package com.waka.techoadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.waka.techoadmin.R;
import com.waka.techoadmin.model.Tag;

import java.util.ArrayList;

public class CategorySlideAdapter extends RecyclerView.Adapter<CategorySlideAdapter.Viewholder> {
    Context context;
    ArrayList<Tag> tagsArrayList;

    public CategorySlideAdapter(Context context, ArrayList<Tag> tagsArrayList){
        this.context = context;
        this.tagsArrayList = tagsArrayList;
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        Button tagButton;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tagButton = itemView.findViewById(R.id.tagButton);
        }
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_slider,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Tag tags = tagsArrayList.get(position);
        holder.tagButton.setText(tags.getName());;

    }

    @Override
    public int getItemCount() {
        return tagsArrayList.size();
    }
}
