package com.example.library.UI.Activities.LogRegResForEnter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.library.Prevalent.Prevalent;
import com.example.library.R;
import com.example.library.UI.Activities.HomeActivity;
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
    private TextView forgetPassword;
    private CheckBox checkBox;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int DOUBLE_BACK_PRESS_INTERVAL = 2000;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(loginActivity.this, "Нажмите 'Назад' ещё раз для выхода", Toast.LENGTH_SHORT).show();

            new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, DOUBLE_BACK_PRESS_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        Paper.init(this);

        forgetPassword = findViewById(R.id.forgetPassword);
        loginBtn = findViewById(R.id.loginBtn);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loadingBar = new ProgressDialog(this);
        checkBox = findViewById(R.id.checkBoxBlvme);


        forgetPassword.setOnClickListener(view -> {
            startActivity(new Intent(loginActivity.this, ResetPasswordActivity.class));
        });
        loginBtn.setOnClickListener(v -> loginUser());
    }

    private void PaperBookWriting(String email, String password) {
        Paper.book().write(Prevalent.UserEmailKey, email);
        Paper.book().write(Prevalent.UserPassword, password);
    }

    private void loginUser() {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        loadingBar.setTitle("Вход в приложение");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Введите почту", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.show();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(loginActivity.this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();
                        if (checkBox.isChecked()) {
                            PaperBookWriting(email, password);
                        }
                        Intent intent = new Intent(loginActivity.this, HomeActivity.class);
                        intent.putExtra("fromlogin", true);
                        startActivity(intent);
                        finish();
                    } else {
                        String messageError = task.getException().getMessage();
                        loadingBar.dismiss();
                        Toast.makeText(loginActivity.this, "Неверные данные", Toast.LENGTH_SHORT).show();
                        Log.d("Sign in error", "Неверные данные" + messageError);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(loginActivity.this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Log.d("On Login", "onComplete: " + e.getMessage());
                    startActivity(new Intent(loginActivity.this, regActivity.class));
                }
            });
        }
    }
}

