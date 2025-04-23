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
import com.example.library.Model.CartItem;
import com.example.library.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bookName.setText(item.getBook().getName());
        holder.bookAuthor.setText(item.getBook().getAuthor());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.price.setText(String.valueOf(item.getBook().getPrice() * item.getQuantity()));

        Glide.with(context).load(item.getBook().getImg_url()).into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookName, bookAuthor, quantity, price;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.cartImageDetail);
            bookName = itemView.findViewById(R.id.cartNameDetail);
            bookAuthor = itemView.findViewById(R.id.cartAuthorDetail);
            quantity = itemView.findViewById(R.id.count);
            price = itemView.findViewById(R.id.cartPriceDetail);
        }
    }
}