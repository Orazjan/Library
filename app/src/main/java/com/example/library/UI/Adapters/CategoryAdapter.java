package com.example.library.UI.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.library.Model.CategoryModel;
import com.example.library.R;
import com.example.library.UI.Activities.ShowAllActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private List<CategoryModel> list;

    public CategoryAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.list = categoryModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.Category_image);
        holder.Category_name.setText(list.get(position).getName());

        holder.itemView.setOnClickListener(v -> { // Используем лямбда-выражение для краткости
            Log.d("CLICK", "Item clicked at position: " + position); // Проверка, что нажатие обрабатывается
            Intent intent = new Intent(holder.itemView.getContext(), ShowAllActivity.class); // Получаем контекст от itemView
            intent.putExtra("type", list.get(position).getType());
            holder.itemView.getContext().startActivity(intent); // Запускаем Activity с правильным контекстом
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView Category_image;
        TextView Category_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Category_image = itemView.findViewById(R.id.Category_image);
            Category_name = itemView.findViewById(R.id.Category_name);
        }
    }
}
