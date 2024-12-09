package com.example.library.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.library.Model.Books;
import com.example.library.R;

import java.util.List;

public class NewBooksAdapter extends RecyclerView.Adapter<NewBooksAdapter.ViewHolder> {
    private Context context;
    private List<Books> list;

    public NewBooksAdapter(Context context, List<Books> books) {
        this.context = context;
        this.list = books;
    }

    @NonNull
    @Override
    public NewBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_books, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull NewBooksAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.new_img);
        holder.book_name.setText(list.get(position).getName());
        holder.author_name.setText(list.get(position).getAuthor());
        holder.price.setText(String.valueOf(list.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        if (list.size() > 2) {
            return list.size() - 5;
        } else {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
