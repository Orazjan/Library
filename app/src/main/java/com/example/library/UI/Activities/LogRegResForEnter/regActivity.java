package com.example.library.UI.Activities.LogRegResForEnter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.Prevalent.Prevalent;
import com.example.library.R;
import com.example.library.UI.Activities.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import io.paperdb.Paper;

public class regActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private Button btnReg;
    private TextInputEditText mailEditText, passwordEditText;
    private TextInputLayout mailInputLayout, passwordInputLayout;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Paper.init(this);
        Paper.book().destroy();
        auth = FirebaseAuth.getInstance();
        btnReg = findViewById(R.id.btnReg);
        passwordEditText = findViewById(R.id.passwordEditText);
        mailEditText = findViewById(R.id.mailEditText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        mailInputLayout = findViewById(R.id.mailInputLayout);
        progressBar = new ProgressDialog(this);

        mailEditText.addTextChangedListener(new TextWatcher() {
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
                updateStatusRegBtn();
            }
        });

        btnReg.setOnClickListener(view -> regAccaunt());
    }

    private boolean isValidPassword(String password) {
        passwordInputLayout.setErrorEnabled(false);
        if (password.isEmpty()) {
            passwordInputLayout.setError("Введите пароль");
            return false;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Длина пароля не может быть меньше 6и");
            return false;
        } else if (password.equals("qwerty")) {
            passwordInputLayout.setError("Серьёзно? qwerty?");
            passwordEditText.setText("");
            return false;
        }
        return true;
    }

    private boolean isValidMail(String mail) {
        mailInputLayout.setErrorEnabled(false);
        if (mail.isEmpty()) {
            mailInputLayout.setError("Введите почту");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            mailInputLayout.setError("Неправильный формат почты");
            return false;
        }
        return true;
    }

    private void updateStatusRegBtn() {
        String mail = Objects.requireNonNull(mailEditText.getText()).toString();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        btnReg.setBackgroundColor(getResources().getColor(R.color.black));
        btnReg.setEnabled(isValidMail(mail) && isValidPassword(password));
    }

    private void regAccaunt() {
        String useremail = Objects.requireNonNull(mailEditText.getText()).toString().trim();
        String userpassword = Objects.requireNonNull(passwordEditText.getText()).toString();
        progressBar.setTitle("Создание аккаунта");
        progressBar.setMessage("Пожалуйста подождите...");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();
        showRememberMeDialog(useremail, userpassword);

        auth.createUserWithEmailAndPassword(useremail, userpassword)
                    .addOnCompleteListener(regActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    sendVerificationEmail(user);
                                }
                                progressBar.dismiss();
                            } else {
                                String errorMessage = "Ошибка регистрации! Неправильные данные";
                                if (task.getException() != null) {
                                    errorMessage = task.getException().getMessage();
                                    Log.e("Firebase Registration", "Error: ", task.getException());
                                }
                                Log.d("ОШИБКА ПРИ РЕГИСТРАЦИИ", errorMessage);
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

    private void sendVerificationEmail(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(regActivity.this, "Подтвердите почту", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(regActivity.this, "Не удалось отправить письмо для подтверждения.", Toast.LENGTH_SHORT).show();
                        Log.e("Email Verification", "Send failed.", task.getException());
                    }
                }
            });
        }
    }

    private void showRememberMeDialog(String username, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Запомнить вас для автовхода?");

        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PaperBookWriting(username, password);

                Intent intent = new Intent(regActivity.this, SettingsActivity.class);

                intent.putExtra("fromRegActivity", true); // <-- Устанавливаем в true

                Log.d("RegActivity", "User chose 'Yes'. Launching SettingsActivity with fromRegActivity: " + intent.getBooleanExtra("fromRegActivity", false));
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(regActivity.this, SettingsActivity.class);
                intent.putExtra("fromRegActivity", true);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void PaperBookWriting(String email, String password) {
        Paper.book().write(Prevalent.UserEmailKey, email);
        Paper.book().write(Prevalent.UserPassword, password);
    }
}