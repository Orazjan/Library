package com.example.library.UI.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.CartItem;
import com.example.library.Prevalent.CartManager;
import com.example.library.R;
import com.example.library.UI.Adapters.CartAdapter;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        adapter = new CartAdapter(this, cartItems);
        recyclerView.setAdapter(adapter);
    }
}