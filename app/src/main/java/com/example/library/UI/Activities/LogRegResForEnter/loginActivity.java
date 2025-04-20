package com.example.library.UI.Activities.LogRegResForEnter;

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

import com.example.library.R;
import com.example.library.UI.Activities.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.library.Prevalent.Prevalent;

import io.paperdb.Paper;


public class loginActivity extends AppCompatActivity {

    private Button loginBtn;
    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private ProgressDialog loadingBar;
    private TextView forgetPassword;
    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        Paper.book().destroy();
                        startActivity(new Intent(loginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        String messageError = task.getException().getMessage();
                        loadingBar.dismiss();
                        Toast.makeText(loginActivity.this, "Ошибка входа! Не правильные данные", Toast.LENGTH_SHORT).show();
                        Log.d("Sign in error", "Ошибка при входе" + messageError);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(loginActivity.this, "Ошибка входа! Не удалось войти!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Log.d("On Login", "onComplete: " + e.getMessage());
                }
            });
        }
    }
}

