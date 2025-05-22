package com.example.library.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.library.Model.Books;
import com.example.library.Model.CartItem;
import com.example.library.Model.CategoryModel;
import com.example.library.R;
import com.example.library.UI.Activities.LogRegResForEnter.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailed_img, total_minus, total_plus;
    TextView detailed_author, detailed_name, detailed_desc, detailed_price, detailed_total, rating;
    Button add_to_cart, buy_now;
    Books showAllModel = null;
    CategoryModel categoryModel = null;
    List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cartItems = new ArrayList<>();
        final Object obj = getIntent().getSerializableExtra("detailed");
        if (obj instanceof Books) {
            showAllModel = (Books) obj;
        } else if (obj instanceof CategoryModel) {
            categoryModel = (CategoryModel) obj;
        }

        detailed_img = findViewById(R.id.detailed_img);

        total_minus = findViewById(R.id.total_minus);
        total_plus = findViewById(R.id.total_plus);

        detailed_author = findViewById(R.id.detailed_author);
        detailed_name = findViewById(R.id.detailed_name);
        detailed_desc = findViewById(R.id.detailed_desc);
        detailed_price = findViewById(R.id.detailed_price);
        detailed_total = findViewById(R.id.detailed_total);
        rating = findViewById(R.id.rating);

        add_to_cart = findViewById(R.id.add_to_cart);
        buy_now = findViewById(R.id.buy_now);

        if (categoryModel != null) {
            Glide.with(getApplicationContext()).load(categoryModel.getImg_url()).into(detailed_img);
            detailed_name.setText(categoryModel.getName());
        }

        if (showAllModel != null) {
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailed_img);
            detailed_author.setText(showAllModel.getAuthor());
            detailed_name.setText(showAllModel.getName());
            detailed_price.setText(String.valueOf(showAllModel.getPrice()));
            detailed_desc.setText(showAllModel.getDescription());
            rating.setText(String.valueOf(showAllModel.getRate()));
        }

        add_to_cart.setOnClickListener(v -> {
            if (showAllModel != null) {
                int quantity = Integer.parseInt(detailed_total.getText().toString());
                addToCart(showAllModel, quantity);
                Toast.makeText(this, "Книга добавлена в корзину", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ошибка: информация о книге не загружена", Toast.LENGTH_SHORT).show();
            }
        });

        total_plus.setOnClickListener(v -> {
            int total = Integer.parseInt(detailed_total.getText().toString());
            total++;
            detailed_total.setText(String.valueOf(total));

        });
        total_minus.setOnClickListener(v -> {
            int total = Integer.parseInt(detailed_total.getText().toString());
            if (total > 1) {
                total--;
                detailed_total.setText(String.valueOf(total));
            }
        });
        buy_now.setOnClickListener(v -> {
            Intent intent = new Intent(this, PayActivity.class);
            int totalPrice = Integer.parseInt(detailed_price.getText().toString()) * Integer.parseInt(detailed_total.getText().toString());
            intent.putExtra("totalPrice", totalPrice);
            startActivity(intent);
            finish();
        });
    }

    public void addToCart(Books book, int quantity) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (cartItems != null) {
                for (CartItem item : cartItems) {
                    if (item.getBook().getId() == book.getId()) {
                        updateQuantityInFirestore(item, item.getQuantity() + quantity,
                                cartItems.indexOf(item));
                        Toast.makeText(this, "Количество обновлено", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            CartItem newItem = new CartItem(book, quantity);
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .collection("cart")
                    .add(newItem)
                    .addOnSuccessListener(documentReference -> {
                        newItem.setDocumentId(documentReference.getId());
                        if (cartItems == null) {
                            cartItems = new ArrayList<>();
                        }
                        cartItems.add(newItem);
                        Toast.makeText(this, "Добавлено в корзину", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, loginActivity.class));
        }
    }

    private void updateQuantityInFirestore(CartItem item, int newQuantity, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .collection("cart")
                    .document(item.getDocumentId())
                    .update("quantity", newQuantity)
                    .addOnSuccessListener(aVoid -> {
                        item.setQuantity(newQuantity);
                        Toast.makeText(this, "Количество обновлено", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}

