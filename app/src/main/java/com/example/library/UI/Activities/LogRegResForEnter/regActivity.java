package com.example.library.UI.Activities.LogRegResForEnter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

            auth.createUserWithEmailAndPassword(useremail, userpassword)
                    .addOnCompleteListener(regActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    sendVerificationEmail(user);
                                }
                                showRememberMeDialog(useremail, userpassword);
                                startActivity(new Intent(regActivity.this, SettingsActivity.class));
                                progressBar.dismiss();
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

    private void sendVerificationEmail(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(regActivity.this, "Письмо для подтверждения отправлено на " + user.getEmail(), Toast.LENGTH_LONG).show();
                        showConfirmationDialog(user.getEmail());
                    } else {
                        Toast.makeText(regActivity.this, "Не удалось отправить письмо для подтверждения.", Toast.LENGTH_SHORT).show();
                        Log.e("Email Verification", "Send failed.", task.getException());
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
                Toast.makeText(regActivity.this, "Подтверждено. Пожалуйста, проверьте вашу почту и перейдите по ссылке.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(regActivity.this, "Подтверждение отменено.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showRememberMeDialog(String username, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Запомнить вас для автовхода?");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_remember_me, null);

        builder.setView(view);

        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PaperBookWriting(username, password);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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