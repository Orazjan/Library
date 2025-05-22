package com.example.library.UI.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.CartItem;
import com.example.library.R;
import com.example.library.UI.Adapters.CartAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private Button btnBuy;
    private TextView totalPriceView;
    private List<CartItem> cartItems = new ArrayList<>();
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        btnBuy = findViewById(R.id.btnBuy);
        totalPriceView = findViewById(R.id.total_price);
        recyclerView = findViewById(R.id.cart_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, cartItems);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    CartItem itemToDelete = cartItems.get(position);

                    adapter.removeItemBySwipe(itemToDelete.getDocumentId());
                }
            }
        }).attachToRecyclerView(recyclerView);

        loadCartItems();

        btnBuy.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                Intent intent = new Intent(this, PayActivity.class);
                int totalPrice = total;
                intent.putExtra("totalPrice", totalPrice);
                startActivity(intent);
            }
        });

    }

    private void loadCartItems() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .collection("cart")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.d("Cart activity", "Ошибка загрузки корзины");
                            return;
                        }

                        if (value != null) {
                            cartItems.clear();

                            for (DocumentSnapshot doc : value.getDocuments()) {
                                String bookId = doc.getId();
                                CartItem item = doc.toObject(CartItem.class);
                                if (item != null) {
                                    item.setDocumentId(bookId); // Устанавливаем ID документа (который является ID книги)
                                    cartItems.add(item);
                                }
                            }

                            adapter.setCartItems(cartItems);
                            adapter.notifyDataSetChanged();
                            updateTotalPrice();
                        }
                    });
        } else {
            Log.d("Cart activity", "Пользователь не авторизован");
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateTotalPrice() {
        runOnUiThread(() -> {
            int currentTotal = 0;
            for (CartItem item : cartItems) {
                if (item != null && item.getBook() != null) {
                    currentTotal += item.getBook().getPrice() * item.getQuantity();
                }
            }
            total = currentTotal;
            btnBuy.setText("Купить сейчас за " + total + " сом");

            if (cartItems.isEmpty()) {
                btnBuy.setEnabled(false);
                btnBuy.setText("Корзина пустая");
            } else {
                btnBuy.setEnabled(true);
            }
        });
    }
}