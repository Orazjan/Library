package com.example.library.UI.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.library.Model.Books;
import com.example.library.Model.CategoryModel;
import com.example.library.R;
import com.example.library.UI.Adapters.CategoryAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailed_img;
    ImageView total_minus, total_plus;
    TextView detailed_author, detailed_name, detailed_desc, detailed_price, detailed_total, rating;
    Button add_to_cart, buy_now;
    Books showAllModel = null;
    Books popularBooksModel = null;

    CategoryModel categoryModel = null;
    CategoryAdapter categoryAdapter;


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

        FirebaseFirestore fireBaseStone = FirebaseFirestore.getInstance();

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
    }
}
