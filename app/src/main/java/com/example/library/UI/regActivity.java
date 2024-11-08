package com.example.library.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class regActivity extends AppCompatActivity {
    Button regButton;
    private EditText name, fam, number, regPassword;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        regButton = findViewById(R.id.regButton);
        name = findViewById(R.id.name);
        fam = findViewById(R.id.fam);
        number = findViewById(R.id.number);
        regPassword = findViewById(R.id.reg_password);
        progressBar = new ProgressDialog(this);

        regButton.setOnClickListener(view -> regAccaunt());
    }

    private void regAccaunt() {
        String username = name.getText().toString();
        String userfam = fam.getText().toString();
        String usernumber = number.getText().toString();
        String userpassword = regPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(userfam) || TextUtils.isEmpty(usernumber) || TextUtils.isEmpty(userpassword)) {
            Toast.makeText(this, "Поле не заполнено!", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setTitle("Создание аккаунта");
            progressBar.setMessage("Пожалуйста подождите...");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            ValidatePhone(username, userfam, usernumber, userpassword);
        }
    }

    private void ValidatePhone(String username, String userfam, String usernumber, String userpassword) {
        final DatabaseReference dbr;
        dbr = FirebaseDatabase.getInstance().getReference();
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(usernumber).exists())) {
                    HashMap<String, Object> usermap = new HashMap<>();
                    usermap.put("phone", usernumber);
                    usermap.put("name", username);
                    usermap.put("fam", userfam);
                    usermap.put("password", userpassword);
                    dbr.child("Users").child(usernumber).updateChildren(usermap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(regActivity.this, "Аккаунт создан", Toast.LENGTH_SHORT).show();
                            Intent loginintent = new Intent(regActivity.this, loginActivity.class);
                            progressBar.dismiss();
                            startActivity(loginintent);

                        }
                    });
                } else {
                    progressBar.dismiss();
                    Toast.makeText(regActivity.this, "Данный номер " + usernumber + " уже зарегестрирован", Toast.LENGTH_SHORT).show();
                    Intent loginintent = new Intent(regActivity.this, loginActivity.class);
                    startActivity(loginintent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}