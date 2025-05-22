package com.example.library.UI.Activities;

import static android.view.View.INVISIBLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PayActivity extends AppCompatActivity {
    private Spinner myCardSpinner, myGetSpinner;
    private TextView emailForSend;
    private ArrayList<String> cardNumbersList;
    private ArrayList<String> getList;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> getAdapter;
    private TextInputLayout countryInputLayout, streetInputLayout, houseInputLayout, homeInputLayout, cityInputLayout;
    private TextInputEditText countryEditText, streetEditText, houseEditText, homeEditText, cityEditText;
    private Button saveData, pay;
    private RelativeLayout getInfoHome, addressFormLayout;
    private String cardNumber;
    private boolean methodSelected, cardSelected = false;
    private ProgressBar progressBar;
    private int completedTasks = 0;
    private final int TOTAL_TASKS = 2;
    private int totalPrice;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar = findViewById(R.id.progressBar);
        myCardSpinner = findViewById(R.id.myCardSpinner);
        myGetSpinner = findViewById(R.id.myGetSpinner);
        getInfoHome = findViewById(R.id.getInfoHome);
        emailForSend = findViewById(R.id.emailForSend);
        countryInputLayout = findViewById(R.id.countryInputLayout);
        streetInputLayout = findViewById(R.id.streetInputLayout);
        houseInputLayout = findViewById(R.id.houseInputLayout);
        homeInputLayout = findViewById(R.id.homeInputLayout);
        cityInputLayout = findViewById(R.id.cityInputLayout);
        countryEditText = findViewById(R.id.countryEditText);
        cityEditText = findViewById(R.id.cityEditText);
        streetEditText = findViewById(R.id.streetEditText);
        houseEditText = findViewById(R.id.houseEditText);
        homeEditText = findViewById(R.id.homeEditText);
        saveData = findViewById(R.id.btnSaveData);
        pay = findViewById(R.id.btnPay);
        cardNumbersList = new ArrayList<>();
        getList = new ArrayList<>();
        saveData.setEnabled(false);
        pay.setEnabled(false);
        addressFormLayout = findViewById(R.id.addressFormLayout);
        addressFormLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        completedTasks = 0;

        paySum();
        getCards();
        getMethods();

        myCardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();

                if (selected.equals("Добавить карту")) {
                    showDialogForAddingCard();
                    cardSelected = false;
                } else if (selected.equals("Нет карт") || selected.equals("Ошибка загрузки карт") || selected.equals("Войдите, чтобы добавить карту")) {
                    cardSelected = false;
                } else {
                    cardSelected = true;
                }
                checkStatusOfBtnPay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cardSelected = false;
                checkStatusOfBtnPay();
            }
        });
        myGetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (selected.equals("Добавление адреса")) {
                    getInfoHome.setVisibility(View.VISIBLE);
                    emailForSend.setVisibility(INVISIBLE);
                    methodSelected = false;
                    checkStatusOfBtnPay();
                } else if (selected.equals("Электронный вариант")) {
                    getInfoHome.setVisibility(INVISIBLE);
                    emailForSend.setText(currentUser.getEmail());
                    emailForSend.setVisibility(View.VISIBLE);
                    methodSelected = true;
                    checkStatusOfBtnPay();
                } else {
                    methodSelected = true;
                    getInfoHome.setVisibility(INVISIBLE);
                    emailForSend.setVisibility(INVISIBLE);
                    checkStatusOfBtnPay();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                getInfoHome.setVisibility(View.GONE);
                emailForSend.setVisibility(View.GONE);
            }
        });

        countryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isValidateCountry(editable.toString());
                checkStatusOfBtnSave();
            }
        });
        streetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isValidateStreet(editable.toString());
                checkStatusOfBtnSave();
            }
        });
        houseEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isValidateHouse(editable.toString());
                checkStatusOfBtnSave();
            }
        });
        homeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isValidateHome(editable.toString());
                checkStatusOfBtnSave();
            }
        });
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isValidateCity(editable.toString());
                checkStatusOfBtnSave();
            }
        });

        saveData.setOnClickListener(View -> {
            getInfoHome.setVisibility(INVISIBLE);
            addDataToFireBase();
        });

        pay.setOnClickListener(view -> {
            Toast.makeText(this, "Спасибо за покупку!", Toast.LENGTH_SHORT).show();
            processPaymentAndClearCart();
            startActivity(new Intent(PayActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void paySum() {
        if (currentUser != null) {
            totalPrice = getIntent().getIntExtra("totalPrice", 0);
            if (totalPrice > 0) {
                pay.setText("Оплатить " + totalPrice + " сом");
            }

        }
    }

    private boolean isValidateCity(String string) {
        if (string.isEmpty()) {
            cityInputLayout.setError("Поле не может быть пустым");
            return false;
        } else {
            cityInputLayout.setError(null);
            return true;
        }
    }

    private boolean isValidateHouse(String string) {
        if (string.isEmpty()) {
            houseInputLayout.setError("Поле не может быть пустым");
            return false;
        } else {
            houseInputLayout.setError(null);
            return true;
        }
    }

    private boolean isValidateHome(String string) {
        if (string.isEmpty()) {
            homeInputLayout.setError("Поле не может быть пустым");
            return false;
        } else {
            homeInputLayout.setError(null);
            return true;
        }
    }

    private boolean isValidateStreet(String string) {
        if (string.isEmpty()) {
            streetInputLayout.setError("Поле не может быть пустым");
            return false;
        } else {
            streetInputLayout.setError(null);
            return true;
        }
    }

    private boolean isValidateCountry(String country) {
        if (country.isEmpty()) {
            countryInputLayout.setError("Поле не может быть пустым");
            return false;
        } else {
            countryInputLayout.setError(null);
            return true;
        }
    }

    private void getMethods() {
        getList.clear();
        getAdapter = null;

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
                                getList.add(street + " " + house + " кв " + home);
                            }
                        }
                    }
                    getList.add("Электронный вариант");
                    getList.add("Добавление адреса");
                    getAdapter = new ArrayAdapter<>(PayActivity.this, android.R.layout.simple_spinner_item, getList);
                    getAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    myGetSpinner.setAdapter(getAdapter);
                } else {
                    getList.add("Электронный вариант");
                    getList.add("Добавление адреса");
                    Log.d("PayActivity", "Ошибка загрузки данных: " + task.getException().getMessage());
                    getAdapter = new ArrayAdapter<>(PayActivity.this, android.R.layout.simple_spinner_item, getList);
                    getAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    myGetSpinner.setAdapter(getAdapter);
                }
            });
            taskCompleted();
        } else {
            getAdapter = new ArrayAdapter<>(PayActivity.this, android.R.layout.simple_spinner_item, getList);
            getAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myGetSpinner.setAdapter(getAdapter);
            taskCompleted();
        }
    }

    private void getCards() {
        cardNumbersList.clear();

        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).collection("cards").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean userHasActualCards = false;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String fetchedCardNumber = document.getString("cardNumber");
                        String cardType = document.getString("cardType");

                        if (fetchedCardNumber != null) {
                            String visibleDigits;
                            if (fetchedCardNumber.length() >= 8) {
                                visibleDigits = fetchedCardNumber.substring(0, 4) +
                                        "*".repeat(fetchedCardNumber.length() - 8) +
                                        fetchedCardNumber.substring(fetchedCardNumber.length() - 4);
                            } else {
                                visibleDigits = fetchedCardNumber;
                            }

                            String finalCardType = (cardType == null) ? "" : cardType;
                            cardNumbersList.add(visibleDigits + " " + finalCardType);
                            userHasActualCards = true;
                        }
                    }

                    if (userHasActualCards) {
                        cardNumbersList.add("Добавить карту");
                    } else {
                        cardNumbersList.add("Добавить карту");
                    }

                    adapter = new ArrayAdapter<>(PayActivity.this, android.R.layout.simple_spinner_item, cardNumbersList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    myCardSpinner.setAdapter(adapter);

                } else {
                    Log.e("PayActivity", "Ошибка загрузки данных: " + task.getException().getMessage());
                    cardNumbersList.add("Ошибка загрузки карт"); // Информируем пользователя
                    adapter = new ArrayAdapter<>(PayActivity.this, android.R.layout.simple_spinner_item, cardNumbersList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    myCardSpinner.setAdapter(adapter);
                }
                taskCompleted();
            });
        } else {
            cardNumbersList.add("Войдите, чтобы добавить карту"); // Или "Нет карт (пользователь не авторизован)"
            adapter = new ArrayAdapter<>(PayActivity.this, android.R.layout.simple_spinner_item, cardNumbersList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myCardSpinner.setAdapter(adapter);
            taskCompleted();
        }
    }

    private void showDialogForAddingCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение действия");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_adding_card, null);
        TextView message = view.findViewById(R.id.messageTextView);
        message.setText("Хотите добавить карту?");

        builder.setView(view);
        builder.setPositiveButton("Добавить карту", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(PayActivity.this, addCardActivity.class));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("PayActivity", "Отмена");
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkStatusOfBtnSave() {
        if (isValidateCountry(countryEditText.getText().toString()) && isValidateStreet(streetEditText.getText().toString()) && isValidateHouse(houseEditText.getText().toString()) && isValidateHome(homeEditText.getText().toString()) && isValidateCity(cityEditText.getText().toString())) {
            saveData.setEnabled(true);
        } else {
            saveData.setEnabled(false);
        }

    }

    private void checkStatusOfBtnPay() {
        pay.setEnabled(cardSelected && methodSelected);
    }

    private void addDataToFireBase() {
        String country = Objects.requireNonNull(countryEditText.getText()).toString();
        String street = Objects.requireNonNull(streetEditText.getText()).toString();
        String house = Objects.requireNonNull(houseEditText.getText()).toString();
        String home = Objects.requireNonNull(homeEditText.getText()).toString();
        String city = Objects.requireNonNull(cityEditText.getText()).toString();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> userData = new HashMap<>();
            userData.put("country", country);
            userData.put("street", street);
            userData.put("house", house);
            userData.put("home", home);
            userData.put("city", city);
            db.collection("users").document(currentUser.getUid()).collection("adress").document(country).set(userData).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                clearTexts();
            }).addOnFailureListener(runnable -> {
                Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
            });
        }
        getMethods();
    }

    private void clearTexts() {
        countryEditText.setText("");
        streetEditText.setText("");
        houseEditText.setText("");
        homeEditText.setText("");
        cityEditText.setText("");
    }

    private void processPaymentAndClearCart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid()).collection("cart").get() // Получаем все элементы корзины
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("users").document(user.getUid()).collection("cart").document(document.getId()) // Используем ID документа корзины (который является ID книги)
                                        .delete().addOnSuccessListener(aVoid -> {
                                            Log.d("PayActivity", "Книга удалена из корзины: " + document.getId());
                                        }).addOnFailureListener(e -> {
                                            Log.e("PayActivity", "Ошибка удаления книги " + document.getId() + " из корзины: " + e.getMessage());
                                            Toast.makeText(PayActivity.this, "Ошибка при удалении некоторых товаров из корзины.", Toast.LENGTH_SHORT).show();
                                        });
                            }
                            Toast.makeText(PayActivity.this, "Спасибо за покупку! Корзина очищена.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PayActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(PayActivity.this, "Ошибка при получении товаров из корзины.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("PayActivity", "Пользователь не найден");
        }
    }

    private void taskCompleted() {
        completedTasks++;
        if (completedTasks >= TOTAL_TASKS) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}