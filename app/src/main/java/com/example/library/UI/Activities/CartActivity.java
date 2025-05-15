package com.example.library.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

        loadCartItems();

        btnBuy.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, PayActivity.class));
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
                            Toast.makeText(this, "Ошибка загрузки корзины", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            cartItems.clear();

                            for (DocumentSnapshot doc : value.getDocuments()) {
                                CartItem item = doc.toObject(CartItem.class);
                                if (item != null) {
                                    item.setDocumentId(doc.getId());
                                    cartItems.add(item);
                                }
                            }

                            adapter.setCartItems(cartItems);
                            adapter.notifyDataSetChanged();
                            updateTotalPrice();
                        }
                    });
        } else {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void updateTotalPrice() {
        runOnUiThread(() -> {
            int total = 0;
            for (CartItem item : cartItems) {
                total += item.getBook().getPrice() * item.getQuantity();
            }
            btnBuy.setText("Купить сейчас за " + total + " сом");

            if (cartItems.isEmpty()) {
                btnBuy.setEnabled(false);
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            }
        });
    }
}