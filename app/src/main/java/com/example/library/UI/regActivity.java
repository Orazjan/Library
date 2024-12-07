package com.example.library.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class regActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private Button regButton;
    private EditText userName, userEmail, userPassword;
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

        auth = FirebaseAuth.getInstance();
        regButton = findViewById(R.id.regButton);
        userName = findViewById(R.id.userName);
        userPassword = findViewById(R.id.userPassword);
        userEmail = findViewById(R.id.userEmail);
        progressBar = new ProgressDialog(this);

        regButton.setOnClickListener(view -> regAccaunt());
    }

    private void regAccaunt() {
        String username = userName.getText().toString();
        String useremail = userEmail.getText().toString();
        String userpassword = userPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(useremail) || TextUtils.isEmpty(userpassword)) {
            Toast.makeText(this, "Поле не заполнено!", Toast.LENGTH_SHORT).show();
            return;
        } else if (userpassword.length() < 6) {
            Toast.makeText(this, "Пароль должен быть больше 6 символов", Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressBar.setTitle("Создание аккаунта");
            progressBar.setMessage("Пожалуйста подождите...");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            auth.createUserWithEmailAndPassword(useremail, userpassword).addOnCompleteListener(regActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(regActivity.this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();
                        startActivity(new Intent(regActivity.this, loginActivity.class));
                    } else {
                        Toast.makeText(regActivity.this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();
                    }
                }
            }).addOnFailureListener(regActivity.this, e -> {
                Toast.makeText(regActivity.this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
                Log.d("On Registration", "onComplete: " + e.getMessage());
            });
        }
    }
}