package com.example.library.UI.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;



public class SettingsActivity extends AppCompatActivity {
    private Button saveSettingsTv, changeBtn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        changeBtn = findViewById(R.id.btnChangeEmail);
        saveSettingsTv = findViewById(R.id.save_settings_tv);
        mAuth = FirebaseAuth.getInstance();

        saveSettingsTv.setOnClickListener(view ->

        {
            new Intent(this, HomeActivity.class);
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeEmailDialog();

            }
        });
    }

    private void showChangeEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Смена электронной почты");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_email, null);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        EditText newEmailEditText = view.findViewById(R.id.newEmailEditText);
        Button positiveButton = null; // Инициализируем позже

        messageTextView.setText("Напишите новую почту на которую вы хотите изменить");

        builder.setView(view);

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Подтвердить", null); // Устанавливаем null listener сначала

        AlertDialog dialog = builder.create();
        dialog.show();

        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        final Button finalPositiveButton = positiveButton;
        final EditText finalNewEmailEditText = newEmailEditText;

        newEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isEmailValid = !TextUtils.isEmpty(s) && android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
                finalPositiveButton.setEnabled(isEmailValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = finalNewEmailEditText.getText().toString().trim();

                if (TextUtils.isEmpty(newEmail) || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    Toast.makeText(SettingsActivity.this, "Пожалуйста, введите корректный email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.fetchSignInMethodsForEmail(newEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                SignInMethodQueryResult result = task.getResult();
                                if (result != null && result.getSignInMethods().size() > 0) {
                                    Toast.makeText(SettingsActivity.this, "Этот адрес электронной почты уже используется.", Toast.LENGTH_LONG).show();
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        user.verifyBeforeUpdateEmail(newEmail)
                                                .addOnCompleteListener(updateTask -> {
                                                    if (updateTask.isSuccessful()) {
                                                        Toast.makeText(SettingsActivity.this, "На ваш новый адрес электронной почты отправлено письмо для подтверждения.", Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    } else {
                                                        Toast.makeText(SettingsActivity.this, "Не удалось отправить письмо для подтверждения нового email.", Toast.LENGTH_SHORT).show();
                                                        if (updateTask.getException() != null) {
                                                            Toast.makeText(SettingsActivity.this, updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "Пользователь не авторизован.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(SettingsActivity.this, "Ошибка при проверке существования email.", Toast.LENGTH_SHORT).show();
                                if (task.getException() != null) {
                                    Toast.makeText(SettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}