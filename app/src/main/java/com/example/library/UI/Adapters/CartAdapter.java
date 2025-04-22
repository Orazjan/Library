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
import com.example.library.Model.CartModel;
import com.example.library.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    List<CartModel> CartList;
    private Context context;

    public CartAdapter(Context context, List<CartModel> CartList) {
        this.context = context;
        this.CartList = CartList;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        if (context != null) {
            Glide.with(context).load(CartList.get(position).getImg_url()).into(holder.cartImageDetail);
        }
        holder.cartAuthorDetail.setText(CartList.get(position).getAuthor());
        holder.cartNameDetail.setText(CartList.get(position).getName());
        holder.cartPriceDetail.setText(String.valueOf(CartList.get(position).getPrice()));
        holder.count.setText(String.valueOf(CartList.get(position).getCount()));
    }

    @Override
    public int getItemCount() {
        return CartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cartImageDetail;
        TextView cartAuthorDetail;
        TextView cartNameDetail;
        TextView cartPriceDetail;
        TextView count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cartImageDetail = itemView.findViewById(R.id.cartImageDetail);
            cartAuthorDetail = itemView.findViewById(R.id.cartAuthorDetail);
            cartNameDetail = itemView.findViewById(R.id.cartNameDetail);
            cartPriceDetail = itemView.findViewById(R.id.cartPriceDetail);
            count = itemView.findViewById(R.id.count);

        }
    }
}
