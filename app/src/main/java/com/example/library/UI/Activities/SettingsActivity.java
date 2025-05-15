package com.example.library.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private Button saveSettingsTv, changeBtn, connectCart;
    private FirebaseAuth mAuth;
    private TextInputEditText userName, userFamily;
    private TextInputLayout textuserName, textuserfam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        changeBtn = findViewById(R.id.btnChangeEmail);
        saveSettingsTv = findViewById(R.id.save_settings_tv);
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.userName);
        userFamily = findViewById(R.id.userfam);
        connectCart = findViewById(R.id.btnConnectCard);
        textuserName = findViewById(R.id.textuserName);
        textuserfam = findViewById(R.id.textuserfam);

        checkStatusOfBtn(false);
        validNamAndFam();
        settingUsernameAndFam();

        saveSettingsTv.setOnClickListener(view ->
        {
            saveUserDataToFirebaseFirestore();
            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            intent.putExtra("fromSettings", true);
            startActivity(intent);
            finish();
        });

        changeBtn.setOnClickListener(View -> {
            showChangeEmailDialog();
        });

        connectCart.setOnClickListener(View -> {
            Intent intent = new Intent(SettingsActivity.this, addCardActivity.class);
            intent.putExtra("open_add_card", true);
            startActivity(intent);
        });
    }

    private void validNamAndFam() {
        userFamily.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!Objects.requireNonNull(userName.getText()).toString().isEmpty() && !editable.toString().isEmpty()) {
                    checkStatusOfBtn(true);
                } else {
                    userFamily.setError("Поле не может быть пустым");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fromSettings", true);
        startActivity(intent);
        finish();
    }

    private void saveUserDataToFirebaseFirestore() {
        String name = userName.getText().toString().trim();
        String family = userFamily.getText().toString().trim();

        if (!name.isEmpty() && !family.isEmpty()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                checkStatusOfBtn(true);
                String uid = currentUser.getUid();
                String email = currentUser.getEmail();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> userData = new HashMap<>();
                userData.put("username", name);
                userData.put("userFam", family);
                userData.put("userMail", email);

                db.collection("users").document(uid)
                        .collection("userInfo").document(email).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
                            checkStatusOfBtn(true);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Ошибка сохранения данных в Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            saveSettingsTv.setEnabled(false);
        }
    }

    private void checkStatusOfBtn(boolean status) {
        saveSettingsTv.setEnabled(status);
    }

    private void settingUsernameAndFam() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid()).collection("userInfo").document(currentUser.getEmail())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                            @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Toast.makeText(SettingsActivity.this, "Ошибка загрузки данных: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                String username = documentSnapshot.getString("username");
                                String userFam = documentSnapshot.getString("userFam");
                                userName.setText(username);
                                userFamily.setText(userFam);
                                checkStatusOfBtn(true);
                                Log.d("FirebaseData", "Username (from userInfo): " + username + ", Family (from userInfo): " + userFam);
                            } else {
                                checkStatusOfBtn(false);
                                Log.d("FirebaseData", "Документ userInfo не существует");
                            }
                        }
                    });
        }
    }

    private void showChangeEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Смена электронной почты");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_email, null);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        EditText newEmailEditText = view.findViewById(R.id.newEmailEditText);

        messageTextView.setText("Напишите новую почту на которую вы хотите изменить");

        builder.setView(view);

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Подтвердить", null); // Устанавливаем null listener сначала

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        final EditText finalNewEmailEditText = newEmailEditText;
        final AlertDialog finalDialog = dialog;
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        newEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ничего не делаем
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isEmailValid = !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches();
                positiveButton.setEnabled(isEmailValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Ничего не делаем
            }
        });

        positiveButton.setOnClickListener(v -> {
            String newEmail = finalNewEmailEditText.getText().toString().trim();

            if (TextUtils.isEmpty(newEmail) || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(SettingsActivity.this, "Пожалуйста, введите корректный email.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUser != null) {
                // Отправляем письмо для подтверждения нового email
                currentUser.verifyBeforeUpdateEmail(newEmail)
                        .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "На ваш новый адрес электронной почты отправлено письмо для подтверждения.", Toast.LENGTH_LONG).show();
                                finalDialog.dismiss();
                                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                                intent.putExtra("fromSettings", true);
                                startActivity(intent);
                                finish();
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
        });
    }
}