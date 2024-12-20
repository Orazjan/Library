package com.example.library.UI.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.library.Model.showAllModel;
import com.example.library.R;
import com.example.library.UI.Activities.DetailedActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class showAllAdapter extends RecyclerView.Adapter<showAllAdapter.ViewHolder> {
    private Context context;
    private ArrayList<showAllModel> list;

    public showAllAdapter() {
    }

    public showAllAdapter(Context context, List<showAllModel> list) {
        this.context = context;
        this.list = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public showAllAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.show_all_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull showAllAdapter.ViewHolder holder, int position) {
        if (context != null) {
            Glide.with(context).load(list.get(position).getImg_url()).into(holder.item_image);
        }
        holder.item_name.setText(list.get(position).getName());
        holder.item_cost.setText(String.valueOf(list.get(position).getPrice()));
        holder.item_author.setText(list.get(position).getAuthor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed", list.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView item_image;
        private TextView item_name;
        private TextView item_cost;
        private TextView item_author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_name = itemView.findViewById(R.id.item_name);
            item_cost = itemView.findViewById(R.id.item_cost);
            item_author = itemView.findViewById(R.id.item_auuthor);
        }
    }
}