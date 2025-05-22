package com.example.library.UI.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.library.Model.CartItem;
import com.example.library.R;
import com.example.library.UI.Activities.CartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
        if (item.getBook() != null) {
            holder.price.setText(String.valueOf(item.getBook().getPrice() * item.getQuantity()));
            Glide.with(context).load(item.getBook().getImg_url()).into(holder.bookImage);
        } else {
            holder.price.setText("N/A");
            holder.bookImage.setImageResource(R.drawable.logotype_foreground); // Замените на вашу картинку по умолчанию
            Log.e("CartAdapter", "Book object is null for item: " + item.getDocumentId());
        }

        holder.itemView.findViewById(R.id.btn_remove).setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Удаление товара")
                    .setMessage("Вы уверены, что хотите удалить этот товар из корзины?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        int currentPosition = holder.getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            CartItem itemToRemove = cartItems.get(currentPosition);
                            removeFromFirestore(itemToRemove.getDocumentId()); // Передаем только documentId
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    public void removeItemBySwipe(String documentId) {
        removeFromFirestore(documentId);
    }

    private void removeFromFirestore(String documentId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .collection("cart")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Товар успешно удален!", Toast.LENGTH_SHORT).show();
                        updateTotalPrice();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Ошибка удаления: ",
                                Toast.LENGTH_SHORT).show();
                        Log.e("CartAdapter", "Ошибка удаления документа: " + documentId + ", Error: " + e.getMessage());
                    });
        }
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

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    private void updateTotalPrice() {
        if (context instanceof CartActivity) {
            ((CartActivity) context).updateTotalPrice();
        }
    }
}