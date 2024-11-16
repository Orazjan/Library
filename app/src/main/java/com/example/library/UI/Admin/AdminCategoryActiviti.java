package com.example.library.UI.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.R;

public class AdminCategoryActiviti extends AppCompatActivity {
    private TextView Category_Fantasy, Category_Drama, Category_Psy, Category_Priklucheniye, Category_Poeziya, Category_Detektiw, Category_Komiksy, Category_LoveRoman, Category_Proza, Category_Trillery, Category_Ujasy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_category_activiti);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast.makeText(this, "Привет, админ", Toast.LENGTH_SHORT).show();
        init();

        Category_Fantasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Fantasy");
                startActivity(intent);
            }
        });
        Category_Detektiw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Detektiw");
                startActivity(intent);

            }
        });
        Category_Drama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Drama");
                startActivity(intent);

            }
        });
        Category_Komiksy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Komiksy");
                startActivity(intent);
            }
        });
        Category_Poeziya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Poeziya");
                startActivity(intent);

            }
        });
        Category_LoveRoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "LoveRoman");
                startActivity(intent);
            }
        });
        Category_Priklucheniye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Priklucheniye");
                startActivity(intent);
            }
        });
        Category_Psy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Psy");
                startActivity(intent);
            }
        });
        Category_Proza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Proza");
                startActivity(intent);
            }
        });
        Category_Trillery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Trillery");
                startActivity(intent);
            }
        });
        Category_Ujasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActiviti.this, AdminAddNewCategoryActivity.class);
                intent.putExtra("Category", "Ujasy");
                startActivity(intent);
            }
        });

    }

    void init() {
        Category_Fantasy = findViewById(R.id.Category_Fantasy);
        Category_Drama = findViewById(R.id.Category_Drama);
        Category_Psy = findViewById(R.id.Category_Psy);
        Category_Priklucheniye = findViewById(R.id.Category_Priklucheniye);
        Category_Poeziya = findViewById(R.id.Category_Poeziya);
        Category_Detektiw = findViewById(R.id.Category_Detektiw);
        Category_Komiksy = findViewById(R.id.Category_Komiksy);
        Category_LoveRoman = findViewById(R.id.Category_LoveRoman);
        Category_Proza = findViewById(R.id.Category_Proza);
        Category_Trillery = findViewById(R.id.Category_Trillery);
        Category_Ujasy = findViewById(R.id.Category_Ujasy);
    }
}