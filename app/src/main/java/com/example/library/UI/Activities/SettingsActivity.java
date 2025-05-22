package com.example.library.UI.Activities;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private Button saveSettingsTv, changeBtn, connectCart, deleteCardbtn, deleteGetPointbtn, addGetPointbtn;
    private FirebaseAuth mAuth;
    private TextInputEditText userName, userFamily;
    private TextInputLayout textuserName, textuserfam;
    private RelativeLayout addressFormLayout, getInfoHome;
    private Spinner mySpinner;
    private ArrayList<String> cardList;
    private ArrayAdapter<String> adapter;
    private LinearLayout linear_layout_get;
    private TextView textViewTop;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
        deleteCardbtn = findViewById(R.id.btnDeleteCard);
        deleteGetPointbtn = findViewById(R.id.btnDeleteGetPoint);
        addGetPointbtn = findViewById(R.id.btnAddGetPoint);
        getInfoHome = findViewById(R.id.getInfoHome);
//        addressFormLayout = findViewById(R.id.addressFormLayout);
        mySpinner = findViewById(R.id.mySpinner);
        linear_layout_get = findViewById(R.id.linear_layout_get);
        cardList = new ArrayList<>();
        textViewTop = findViewById(R.id.textViewTop);
        getInfoHome.setVisibility(VISIBLE);

        Intent intent = getIntent();
        boolean fromRegActivity = intent.getBooleanExtra("fromRegActivity", false);
        if (fromRegActivity) {
            if (deleteCardbtn != null) {
                deleteCardbtn.setEnabled(false);
            }
            if (deleteGetPointbtn != null) {
                deleteGetPointbtn.setEnabled(false);
            }
            if (changeBtn != null) {
                changeBtn.setEnabled(false);
            }
        } else {

            if (deleteCardbtn != null) deleteCardbtn.setEnabled(true);
            if (deleteGetPointbtn != null) deleteGetPointbtn.setEnabled(true);
            if (changeBtn != null) changeBtn.setEnabled(true);
        }

        saveSettingsTv.setEnabled(false);
        setupTextWatchers();
        settingUsernameAndFam();

        saveSettingsTv.setOnClickListener(view -> {
            if (checkFormValidity()) {
                saveUserDataToFirebaseFirestore();
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(SettingsActivity.this, HomeActivity.class);
                homeIntent.putExtra("fromSettings", true);
                startActivity(homeIntent);
                finish();
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля корректно.", Toast.LENGTH_SHORT).show();
            }
        });

        changeBtn.setOnClickListener(View -> {
            showChangeEmailDialog();
        });

        connectCart.setOnClickListener(View -> {
            Intent addCardIntent = new Intent(SettingsActivity.this, addCardActivity.class);
            addCardIntent.putExtra("open_add_card", true);
            startActivity(addCardIntent);
        });

        addGetPointbtn.setOnClickListener(View -> {
            getInfoHome.setVisibility(VISIBLE);
            linear_layout_get.setVisibility(INVISIBLE);
            addressFormLayout.setVisibility(VISIBLE);
        });
        deleteCardbtn.setOnClickListener(View -> {
            getInfoHome.setVisibility(VISIBLE);
            addressFormLayout.setVisibility(INVISIBLE);
            linear_layout_get.setVisibility(VISIBLE);
            getCards();
        });
        deleteGetPointbtn.setOnClickListener(View -> {
            getInfoHome.setVisibility(VISIBLE);
            addressFormLayout.setVisibility(INVISIBLE);
            linear_layout_get.setVisibility(VISIBLE);
            getMethods();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fromSettings", true);
        Toast.makeText(this, "Данные не сохранены", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    private void setupTextWatchers() {
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateUserName();
                checkFormValidity();
            }
        });

        userFamily.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateUserFam();
                checkFormValidity();
            }
        });
    }

    private boolean validateUserName() {
        String name = Objects.requireNonNull(userName.getText()).toString().trim();
        if (name.isEmpty()) {
            textuserName.setError("Поле не может быть пустым");
            return false;
        } else if (name.length() < 2) {
            textuserName.setError("Имя должно содержать не менее 2 символов");
            return false;
        } else {
            textuserName.setError(null);
            return true;
        }
    }

    private boolean validateUserFam() {
        String family = Objects.requireNonNull(userFamily.getText()).toString().trim();
        if (family.isEmpty()) {
            textuserfam.setError("Поле не может быть пустым");
            return false;
        } else if (family.length() < 2) {
            textuserfam.setError("Фамилия должна содержать не менее 2 символов");
            return false;
        } else {
            textuserfam.setError(null);
            return true;
        }
    }

    private boolean checkFormValidity() {
        boolean isNameValid = validateUserName();
        boolean isFamValid = validateUserFam();

        boolean formIsValid = isNameValid && isFamValid;
        saveSettingsTv.setEnabled(formIsValid);
        return formIsValid;
    }

    private void saveUserDataToFirebaseFirestore() {
        String name = Objects.requireNonNull(userName.getText()).toString().trim();
        String family = Objects.requireNonNull(userFamily.getText()).toString().trim();

        if (!name.isEmpty() && !family.isEmpty()) {
            if (currentUser != null) {
                String uid = currentUser.getUid();
                String email = currentUser.getEmail();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> userData = new HashMap<>();
                userData.put("username", name);
                userData.put("userFam", family);

                db.collection("users").document(uid)
                        .collection("userInfo").document(email).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Ошибка сохранения данных в Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
        }
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
                                Log.d("FirebaseData", "Username (from userInfo): " + username + ", Family (from userInfo): " + userFam);

                                checkFormValidity();
                            } else {
                                Log.d("FirebaseData", "Документ userInfo не существует"); // Если данных нет, поля останутся пустыми, и кнопка должна быть неактивна
                                checkFormValidity();
                            }
                        }
                    });
        } else {
            checkFormValidity();
        }
    }

    private void showChangeEmailDialog() {
        // ... (остальной код showChangeEmailDialog остается без изменений)
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

    private void getMethods() {
        textViewTop.setText("Выберите адрес");
        cardList.clear();
        adapter = null;

        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).collection("adress").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    if (document != null && !document.isEmpty()) {
                        for (DocumentSnapshot doc : document) {
                            String street = doc.getString("street");
                            String house = doc.getString("house");
                            String home = doc.getString("home");
                            if (street != null && house != null && home != null) {
                                cardList.add(street + " " + house + " кв " + home);
                            }
                        }
                    }
                    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cardList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mySpinner.setAdapter(adapter);
                }
            });
        } else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cardList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySpinner.setAdapter(adapter);
            deleteGetPointbtn.setEnabled(false);
        }
    }

    private void getCards() {
        textViewTop.setText("Выберите карту");
        cardList.clear();
        adapter = null;

        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).collection("cards").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean hasCards = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String cardNumber = document.getString("cardNumber");
                        String cardType = document.getString("cardType");
                        if (cardType == null) {
                            cardType = " ";
                        }

                        if (cardNumber != null) {
                            cardList.add(cardNumber + " " + cardType);
                            hasCards = true;
                        }
                    }

                    if (hasCards) {
                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cardList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mySpinner.setAdapter(adapter);
                    } else {
                        deleteCardbtn.setEnabled(false);
                    }

                } else {
                    Log.d("SettingsActivity", "Ошибка загрузки данных: " + task.getException().getMessage());
                }
            });
        }
    }
}