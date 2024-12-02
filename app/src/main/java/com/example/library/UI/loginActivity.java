package com.example.library.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.library.Prevalent.Prevalent;
import com.example.library.R;
import com.example.library.UI.Users.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {
    private Button loginBtn;
    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private ProgressDialog loadingBar;
    private CheckBox login_checkbox;
    private TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        forgetPassword = findViewById(R.id.forgetPassword);
        loginBtn = findViewById(R.id.loginBtn);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        login_checkbox = findViewById(R.id.login_checkbox);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginBtn.setOnClickListener(v -> loginUser());
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        loadingBar.setTitle("Вход в приложение");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        if (login_checkbox.isChecked()) {
            Paper.book().write(Prevalent.UserEmailKey, email);
            Paper.book().write(Prevalent.UserPassword, password);
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен быть больше 6 символов", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(loginActivity.this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        startActivity(new Intent(loginActivity.this, HomeActivity.class));
                    } else {
                        Toast.makeText(loginActivity.this, "Ошибка входа!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("On Login", "onComplete: " + e.getMessage());
                }
            });
        }
    }
}

