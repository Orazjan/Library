package com.example.library.UI.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private RelativeLayout getInfoHome;
    private String cardNumber;
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
        getCards();
        getMethods();


        myCardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        myGetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (selected.equals("Выберите вариант получения")) {
                    getInfoHome.setVisibility(View.GONE);
                    emailForSend.setVisibility(View.GONE);
                } else if (selected.equals("Доставка до точки получения")) {
                    getInfoHome.setVisibility(View.VISIBLE);
                    emailForSend.setVisibility(View.INVISIBLE);
                } else {
                    emailForSend.setText(currentUser.getEmail());
                    getInfoHome.setVisibility(View.INVISIBLE);
                    emailForSend.setVisibility(View.VISIBLE);
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
            addDataToFireBase();
        });

        pay.setOnClickListener(view -> {
            Toast.makeText(this, "Оплата скоро пройдёт", Toast.LENGTH_SHORT).show();
        });
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
        getList.add("Выберите вариант получения");
        getList.add("Электронный вариант");
        getList.add("Доставка до точки получения");

        getAdapter = new ArrayAdapter<>(PayActivity.this, android.R.layout.simple_spinner_item, getList);
        getAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myGetSpinner.setAdapter(getAdapter);
    }

    private void getCards() {
        cardNumbersList.clear();
        cardNumbersList.add("Выбрите карту");
        adapter = null;

        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).collection("cards").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean hasCards = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        cardNumber = document.getString("cardNumber");
                        String cardType = document.getString("cardType");
                        String visibleDigits;
                        if (cardType == null) {
                            cardType = " ";
                        }

                        if (cardNumber != null) {
                            visibleDigits = cardNumber.substring(0, 4) + "*".repeat(cardNumber.length() - 8) + cardNumber.substring(cardNumber.length() - 4);
                            cardNumbersList.add(visibleDigits + " " + cardType);
                            hasCards = true;
                        }
                    }

                    if (hasCards) {
                        adapter = new ArrayAdapter<>(PayActivity.this, android.R.layout.simple_spinner_item, cardNumbersList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        myCardSpinner.setAdapter(adapter);
                    } else {
                        showDialogForAddingCard();
                    }

                } else {
                    Toast.makeText(PayActivity.this, "Ошибка загрузки данных: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showDialogForAddingCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение действия");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_adding_card, null);
        TextView message = view.findViewById(R.id.messageTextView);
        message.setText("Вы не имеете активных карт. Хотите добавить карту?");

        builder.setView(view);
        builder.setPositiveButton("Добавить карту", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(PayActivity.this, addCardActivity.class));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(PayActivity.this, "Добавление карты отменено.", Toast.LENGTH_SHORT).show();
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
        myCardSpinner.getSelectedItem();
        myGetSpinner.getSelectedItem();
        if (myCardSpinner.getSelectedItem().equals(cardNumber) && myGetSpinner.getSelectedItem().toString().equals("Выберите вариант получения") || myGetSpinner.getSelectedItem().equals(currentUser.getEmail())) {
            pay.setEnabled(false);
        } else {
            pay.setEnabled(true);
        }
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
            db.collection("users").document(currentUser.getUid()).collection("userInfo").document("adress").set(userData).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(runnable -> {
                Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
            });
        }
    }
}