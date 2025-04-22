package com.example.library.UI.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.CartModel;
import com.example.library.Model.CartViewModel;
import com.example.library.Model.CartViewModelFactory;
import com.example.library.R;
import com.example.library.UI.Adapters.CartAdapter;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    List<CartModel> CartList;
    CartAdapter cartAdapter;
    RecyclerView cartRecyclerView;
    CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart); // Используем макет activity_cart

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cartRecyclerView = findViewById(R.id.cart_fragment_container); // Замените на фактический ID RecyclerView в activity_cart.xml
        CartList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, CartList); // Используем 'this' в качестве Context
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        cartViewModel = new ViewModelProvider(
                this,
                CartViewModelFactory.getInstance(getApplication()) // getInstance — Singleton
        ).get(CartViewModel.class);
        Log.d("CART_DEBUG cartactivity", "Hashcode " + cartViewModel.hashCode());
        cartViewModel.getCartItems().observe(this, new Observer<List<CartModel>>() { // Используем 'this' как LifecycleOwner
            @Override
            public void onChanged(List<CartModel> cartModels) {
                CartList.clear();
                CartList.addAll(cartModels);
                cartAdapter.notifyDataSetChanged();
            }
        });
        Log.d("CART_DEBUG", "CartActivity started. Items: " + cartViewModel.getCartItems().getValue());

    }
}