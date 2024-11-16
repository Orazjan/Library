package com.example.library.UI.Admin;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.text.TextUtils;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.ProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewCategoryActivity extends AppCompatActivity {
    private String categoryName, pName, pDescription, pAuthor, pPrice, CurrentData, CurrentTime, randomKey;
    private ImageView productImage;
    private EditText productTitle, productAuthor, productDescription, productPrice;
    private Button addProduct;
    private static final int GALLERYPICK = 1;
    private Uri imageUri;
    private String downloadImageUrl;
    private StorageReference productImageRef;
    private DatabaseReference productRef;
    private ProgressDialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_new_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        init();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });

    }

    private void validateProductData() {
        pName = productTitle.getText().toString();
        pAuthor = productAuthor.getText().toString();
        pDescription = productDescription.getText().toString();
        pPrice = productPrice.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Изображение не выбрано", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(pName)) {
            Toast.makeText(this, "Введите название книги", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pAuthor)) {
            Toast.makeText(this, "Введите автора книги", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pDescription)) {
            Toast.makeText(this, "Введите описание книги", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pPrice)) {
            Toast.makeText(this, "Введите цену книги", Toast.LENGTH_SHORT).show();
        } else {
            storeProductInformation();
        }
    }

    private void storeProductInformation() {
        progressBar.setTitle("Загрузка фотографии");
        progressBar.setMessage("Пожалуйста подождите...");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();

        Toast.makeText(this, "Сохранение товара", Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        CurrentData = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        CurrentTime = currentTime.format(calendar.getTime());
        randomKey = CurrentData + CurrentTime;

        StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + randomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String err = e.toString();
                Toast.makeText(AdminAddNewCategoryActivity.this, err, Toast.LENGTH_SHORT).show();
                progressBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewCategoryActivity.this, "Успешно", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminAddNewCategoryActivity.this, "Успешно", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", randomKey);
        productMap.put("date", CurrentData);
        productMap.put("time", CurrentTime);
        productMap.put("description", pDescription);
        productMap.put("image", downloadImageUrl);
        productMap.put("author", pAuthor);
        productMap.put("price", pPrice);
        productMap.put("title", pName);
        productMap.put("category", categoryName);

        productRef.child(randomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.dismiss();
                    Toast.makeText(AdminAddNewCategoryActivity.this, "Успешно", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminAddNewCategoryActivity.this, AdminCategoryActiviti.class);
                    startActivity(intent);
                } else {
                    String err = task.getException().toString();
                    Toast.makeText(AdminAddNewCategoryActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);

        }

    }

    private void init() {
        categoryName = getIntent().getExtras().get("Category").toString();
        productImage = findViewById(R.id.cameraLogo);
        productTitle = findViewById(R.id.nameBook);
        productAuthor = findViewById(R.id.nameAwtor);
        productDescription = findViewById(R.id.nameOpisaniye);
        productPrice = findViewById(R.id.cost);
        addProduct = findViewById(R.id.btnAdd);
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");

    }
}