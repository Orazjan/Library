package com.example.library.UI.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.CartItem;
import com.example.library.Prevalent.CartManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        btnBuy = findViewById(R.id.btnBuy);
        recyclerView = findViewById(R.id.cart_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        adapter = new CartAdapter(this, cartItems);
        recyclerView.setAdapter(adapter);
        btnBuy.setText("Купить сейчас за ");
        loadCartItems();
        btnBuy.setOnClickListener(V ->
        {
            Toast.makeText(this, "Здесь должно открываться покупка", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCartItems() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .collection("cart")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<CartItem> cartItems = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            CartItem item = doc.toObject(CartItem.class);
                            if (item != null) {
                                cartItems.add(item);
                            }
                        }

                        // Обновляем адаптер
                        adapter.setCartItems(cartItems);
                        adapter.notifyDataSetChanged();
                    });
        }
    }

    private void removeFromCart(String bookId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(bookId)
                    .delete();
        }
    }
}


