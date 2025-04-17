package com.example.library.UI.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.library.Model.Books;
import com.example.library.Model.PopularBooksModel;
import com.example.library.R;
import com.example.library.UI.Activities.DetailedActivity;

import java.util.ArrayList;
import java.util.List;

public class PopularBooksAdapter extends RecyclerView.Adapter<PopularBooksAdapter.ViewHolder> {
    private List<PopularBooksModel> popularBooksList;
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_popular_books_adapter, parent, false));
    }

    public PopularBooksAdapter(Context context, List<PopularBooksModel> popularBooksList) {
        this.popularBooksList = popularBooksList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull PopularBooksAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(popularBooksList.get(position).getImg_url()).into(holder.new_img);
        holder.book_name.setText(popularBooksList.get(position).getName());
        holder.author_name.setText(popularBooksList.get(position).getAuthor());
        holder.price.setText(String.valueOf(popularBooksList.get(position).getPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed", popularBooksList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularBooksList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView new_img;
        TextView book_name, author_name, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            new_img = itemView.findViewById(R.id.new_img);
            book_name = itemView.findViewById(R.id.book_name);
            author_name = itemView.findViewById(R.id.author_name);
            price = itemView.findViewById(R.id.price);
        }
    }
}