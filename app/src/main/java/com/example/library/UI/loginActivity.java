package com.example.library.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.library.R;
import com.example.library.UI.Admin.AdminCategoryActiviti;
import com.example.library.UI.Users.HomeActiviti;
import com.example.library.Model.Users;
import com.example.library.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText phoneInput, passwordInput;
    private ProgressDialog loadingBar;
    private TextView adminLink, notAdminLink;
    private String parentDbName = "Users";
    private CheckBox login_checkbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);
        phoneInput = findViewById(R.id.login_number);
        passwordInput = findViewById(R.id.login_password);
        adminLink = findViewById(R.id.adminLink);
        notAdminLink = findViewById(R.id.notAdminLink);
        loadingBar = new ProgressDialog(this);
        login_checkbox = findViewById(R.id.login_checkbox);

        Paper.init(this);

        loginBtn.setOnClickListener(v -> loginUser());

        adminLink.setOnClickListener(view -> {
            adminLink.setVisibility(View.INVISIBLE);
            notAdminLink.setVisibility(View.VISIBLE);
            loginBtn.setText("Вход в панель администратора");
            parentDbName = "Admins";
        });
        notAdminLink.setOnClickListener(view -> {
            adminLink.setVisibility(View.VISIBLE);
            notAdminLink.setVisibility(View.INVISIBLE);
            loginBtn.setText("Начать");
            parentDbName = "Users";
        });
    }

    private void loginUser() {
        String phone = phoneInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone, password);
        }
    }

    private void ValidateUser(final String phone, final String password) {

        if (login_checkbox.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPassword, password);
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()) {
                    String userName = dataSnapshot.child(parentDbName).child(phone).child("name").getValue(String.class);
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    assert usersData != null;

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            if (parentDbName.equals("Admins")) {
                                loadingBar.dismiss();
                                Toast.makeText(loginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                                Intent adminIntent = new Intent(loginActivity.this, AdminCategoryActiviti.class);
                                startActivity(adminIntent);
                            } else if (parentDbName.equals("Users")) {
                                loadingBar.dismiss();
                                Toast.makeText(loginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(loginActivity.this, HomeActiviti.class);

                                homeIntent.putExtra("userName", userName.toString());
                                startActivity(homeIntent);
                            }

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(loginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(loginActivity.this, "Аккаунт с номером " + phone + "не существует", Toast.LENGTH_SHORT).show();

                    Intent registerIntent = new Intent(loginActivity.this, regActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

