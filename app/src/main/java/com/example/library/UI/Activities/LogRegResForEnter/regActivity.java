package com.example.library.UI.Activities.LogRegResForEnter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.R;
import com.example.library.UI.Activities.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class regActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private Button regButton;
    private EditText userEmail, userPassword;
    private ProgressDialog progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        regButton = findViewById(R.id.regButton);
        userPassword = findViewById(R.id.userPassword);
        userEmail = findViewById(R.id.userEmail);
        progressBar = new ProgressDialog(this);

        regButton.setOnClickListener(view -> regAccaunt());
    }

    private void sendVerificationEmail(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showConfirmationDialog(user.getEmail());
                    } else {
                        Toast.makeText(regActivity.this, "Не удалось отправить письмо для подтверждения.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showConfirmationDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение действия");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_email, null);
        TextView emailTextView = view.findViewById(R.id.textViewEmail);
        emailTextView.setText(email);

        builder.setView(view);

        builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(regActivity.this, "Подтверждено. Пожалуйста, проверьте вашу почту.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                // После подтверждения можно перенаправить пользователя на другой экран, например, экран входа
                startActivity(new Intent(regActivity.this, SettingsActivity.class)); // Замените LoginActivity на нужный класс
                finish(); // Закрыть Activity регистрации
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(regActivity.this, "Подтверждение отменено.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // Можно добавить дополнительное действие при отмене, например, предложить повторную отправку письма
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void regAccaunt() {
        String useremail = userEmail.getText().toString().trim();
        String userpassword = userPassword.getText().toString();

        if (TextUtils.isEmpty(useremail) || TextUtils.isEmpty(userpassword)) {
            Toast.makeText(this, "Поле не заполнено!", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {
            Toast.makeText(this, "Неверный формат электронной почты", Toast.LENGTH_SHORT).show();
        } else if (userpassword.length() < 6) {
            Toast.makeText(this, "Пароль должен быть больше 6 символов", Toast.LENGTH_SHORT).show();
        } else if (useremail.equals("qwerty")) {
            Toast.makeText(this, "Выберите более надёжный пароль", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setTitle("Создание аккаунта");
            progressBar.setMessage("Пожалуйста подождите...");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            auth.createUserWithEmailAndPassword(useremail, userpassword)
                    .addOnCompleteListener(regActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    sendVerificationEmail(user); // Отправляем письмо после успешной регистрации
                                }
                                Toast.makeText(regActivity.this, "Регистрация прошла успешно! Пожалуйста, подтвердите вашу почту.", Toast.LENGTH_LONG).show();
                                progressBar.dismiss();
                                // Не перенаправляем сразу на SettingsActivity, ждем подтверждения
                            } else {
                                String errorMessage = "Ошибка регистрации! Неправильные данные";
                                if (task.getException() != null) {
                                    errorMessage = task.getException().getMessage();
                                    Log.e("Firebase Registration", "Error: ", task.getException());
                                }
                                Toast.makeText(regActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                progressBar.dismiss();
                            }
                        }
                    })
                    .addOnFailureListener(regActivity.this, e -> {
                        Toast.makeText(regActivity.this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();
                        Log.d("On Registration", "onComplete: " + e.getMessage());
                    });
        }
    }
}