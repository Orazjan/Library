package com.example.library.UI.Activities.LogRegResForEnter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.library.Prevalent.Prevalent;
import com.example.library.R;
import com.example.library.UI.Activities.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import io.paperdb.Paper;


public class loginActivity extends AppCompatActivity {

    private Button loginBtn;
    private FirebaseAuth auth;
    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailInputLayout, passwordInputLayout;
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
        Paper.book().destroy();

        forgetPassword = findViewById(R.id.forgetPassword);
        loginBtn = findViewById(R.id.loginBtn);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        loadingBar = new ProgressDialog(this);
        checkBox = findViewById(R.id.checkBoxBlvme);

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isValidMail(editable.toString());
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isValidPassword(editable.toString());
                updateStatus();
            }
        });

        forgetPassword.setOnClickListener(view -> {
            startActivity(new Intent(loginActivity.this, ResetPasswordActivity.class));
        });
        loginBtn.setOnClickListener(v -> loginUser());
    }


    private boolean isValidMail(String string) {
        if (string.isEmpty()) {
            emailInputLayout.setError("Введите почту");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
            emailInputLayout.setError("Неверный формат почты");
            return false;
        } else {
            emailInputLayout.setError(null);
            return true;
        }
    }

    private boolean isValidPassword(String string) {
        if (string.isEmpty()) {
            passwordInputLayout.setError("Введите пароль");
            return false;
        } else if (string.length() < 6) {
            passwordInputLayout.setError("Пароль должен содержать не менее 6 символов");
            return false;
        } else {
            passwordInputLayout.setError(null);
            return true;
        }
    }

    private void updateStatus() {
        if (isValidMail(Objects.requireNonNull(emailEditText.getText()).toString()) && isValidPassword(Objects.requireNonNull(passwordEditText.getText()).toString())) {
            loginBtn.setEnabled(true);
            loginBtn.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            loginBtn.setEnabled(false);
        }
    }

    private void PaperBookWriting(String email, String password) {
        Paper.book().write(Prevalent.UserEmailKey, email);
        Paper.book().write(Prevalent.UserPassword, password);
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        loadingBar.setTitle("Вход в приложение");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);

        loadingBar.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActivity.this, task -> {
                    loadingBar.dismiss();
                    if (task.isSuccessful()) {
                        if (checkBox.isChecked()) {
                            PaperBookWriting(email, password);
                        }
                        Intent intent = new Intent(loginActivity.this, HomeActivity.class);
                        intent.putExtra("fromlogin", true);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Log.d("Sign in error", "Ошибка входа: " + errorMessage);

                        if (errorMessage.contains("wrong-password") || errorMessage.contains("incorrect password")) {
                            Toast.makeText(loginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(loginActivity.this, "Неверные данные для входа", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    loadingBar.dismiss();
                    Log.e("Login Failure", "Ошибка подключения. Попробуйте позже: " + e.getMessage());
                    Toast.makeText(loginActivity.this, "Ошибка подключения. Попробуйте позже.", Toast.LENGTH_SHORT).show();
                });
    }
}


