package com.example.library.UI.Activities;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentsActivity extends AppCompatActivity {

    private TextInputLayout cardNumberInputLayout;
    private TextInputEditText cardNumberEditText;
    private TextInputLayout expiryMonthInputLayout;
    private TextInputEditText expiryMonthEditText;
    private TextInputLayout expiryYearInputLayout;
    private TextInputEditText expiryYearEditText;
    private TextInputLayout cvvInputLayout;
    private TextInputEditText cvvEditText;
    private Button payButton;
    private ImageView cardTypeImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        cardNumberInputLayout = findViewById(R.id.cardNumberInputLayout);
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expiryMonthInputLayout = findViewById(R.id.expiryMonthInputLayout);
        expiryMonthEditText = findViewById(R.id.expiryMonthEditText);
        expiryYearInputLayout = findViewById(R.id.expiryYearInputLayout);
        expiryYearEditText = findViewById(R.id.expiryYearEditText);
        cvvInputLayout = findViewById(R.id.cvvInputLayout);
        cvvEditText = findViewById(R.id.cvvEditText);
        payButton = findViewById(R.id.payButton);
        cardTypeImageView = findViewById(R.id.cardTypeImageView);


        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String cardNumber = s.toString().replaceAll("\\s+", "");
                String cardType = getCardType(cardNumber);
                cardTypeImageView.setVisibility(View.VISIBLE);
                if (cardType.equals("visa")) {
                    cardTypeImageView.setImageResource(R.drawable.ic_visa);
                } else if (cardType.equals("mastercard")) {
                    cardTypeImageView.setImageResource(R.drawable.ic_mastercard);
                } else {
                    cardTypeImageView.setVisibility(View.GONE); // Скрываем, если тип не определен
                }

                if (!isValidCardNumber(cardNumber) && !cardNumber.isEmpty()) {
                    cardNumberInputLayout.setError("Неверный номер карты");
                } else {
                    cardNumberInputLayout.setError(null);
                }

                updatePayButtonState();
            }
        });

        expiryMonthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidExpiryMonth(s.toString());
                updatePayButtonState();
            }
        });
        expiryYearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidExpiryYear(s.toString());
                updatePayButtonState();
            }
        });

        cvvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 3) {
                    cvvInputLayout.setError("Неверный CVV");
                } else {
                    cvvInputLayout.setError(null);
                }
                updatePayButtonState();
            }
        });

        payButton.setOnClickListener(v -> {
            String cardNumber = Objects.requireNonNull(cardNumberEditText.getText()).toString().replaceAll("\\s+", "");
            String expiryMonth = expiryMonthEditText.getText().toString();
            String expiryYear = expiryYearEditText.getText().toString();
            String cvv = Objects.requireNonNull(cvvEditText.getText()).toString();

            if (isValidCardNumber(cardNumber) && isValidExpiryMonth(expiryMonth) && isValidExpiryYear(expiryYear) && isValidCvv(cvv)) {

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    String userEmail = currentUser.getEmail();
                    Map<String, Object> card = new HashMap<>();
                    card.put("cardNumber", cardNumber);
                    card.put("expiryMonth", expiryMonth);
                    card.put("expiryYear", expiryYear);
                    card.put("cvv", cvv);
                    card.put("userEmail", userEmail);

                    firestore.collection("users")
                            .document(uid)
                            .collection("cards")
                            .add(card)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(PaymentsActivity.this, "Карта успешно добавлена", Toast.LENGTH_SHORT).show();
                                cardNumberEditText.setText("");
                                expiryMonthEditText.setText("");
                                expiryYearEditText.setText("");
                                cvvEditText.setText("");
                                finish();
                                startActivity(new Intent(this, HomeActivity.class));
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(PaymentsActivity.this, "Ошибка добавления карты: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                } else {
                    Toast.makeText(PaymentsActivity.this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("Error", "Error");
            }
        });

        payButton.setEnabled(false);
    }

    private boolean isValidExpiryMonth(String month) {
        if (month.isEmpty()) {
            expiryMonthInputLayout.setError("Введите месяц");
            return false;
        }
        try {
            int m = Integer.parseInt(month);
            if (m < 1 || m > 12) {
                expiryMonthInputLayout.setError("Неверный месяц");
                return false;
            }
            expiryMonthInputLayout.setError(null);
            return true;
        } catch (NumberFormatException e) {
            expiryMonthInputLayout.setError("Неверный формат месяца");
            return false;
        }
    }

    private boolean isValidExpiryYear(String year) {
        if (year.isEmpty()) {
            expiryYearInputLayout.setError("Введите год");
            return false;
        }
        try {
            int y = Integer.parseInt(year);
            int currentYear = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100;
            }
            if (y < currentYear) {
                expiryYearInputLayout.setError("Неверный год");
                return false;
            }
            expiryYearInputLayout.setError(null);
            return true;
        } catch (NumberFormatException e) {
            expiryYearInputLayout.setError("Неверный формат года");
            return false;
        }
    }

    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 13 || cardNumber.length() > 19 || !cardNumber.matches("\\d+")) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private String getCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "visa";
        } else if (cardNumber.startsWith("5")) {
            return "mastercard";
        }
        return "";
    }

    private boolean isValidCvv(String cvv) {
        return cvv != null && (cvv.length() == 3 || cvv.length() == 4) && cvv.matches("\\d+");
    }

    private void updatePayButtonState() {
        String cardNumber = cardNumberEditText.getText().toString().replaceAll("\\s+", "");
        String expiryMonth = expiryMonthEditText.getText().toString();
        String expiryYear = expiryYearEditText.getText().toString();
        String cvv = cvvEditText.getText().toString();

        boolean isCardNumberValid = isValidCardNumber(cardNumber);
        boolean isExpiryMonthValid = isValidExpiryMonth(expiryMonth);
        boolean isExpiryYearValid = isValidExpiryYear(expiryYear);
        boolean isCvvValid = isValidCvv(cvv);

        payButton.setEnabled(isCardNumberValid && isExpiryMonthValid && isExpiryYearValid && isCvvValid);

    }
}