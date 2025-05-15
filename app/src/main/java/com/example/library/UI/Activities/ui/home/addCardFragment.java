package com.example.library.UI.Activities.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.library.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class addCardFragment extends Fragment {

    private TextInputLayout cardNumberInputLayout;
    private TextInputEditText cardNumberEditText;
    private TextInputLayout expiryYearInputLayout, cvvInputLayout, expiryMonthInputLayout, namefamInputLayout;
    private TextInputEditText expiryYearEditText, cvvEditText, expiryMonthEditText, namefamEditText;
    private Button payButton;
    private ImageView cardTypeImageView;
    private String cardType;

    private FirebaseAuth firebaseAuth;

    public addCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.addcardfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardNumberInputLayout = view.findViewById(R.id.cardNumberInputLayout);
        cardNumberEditText = view.findViewById(R.id.cardNumberEditText);
        expiryMonthInputLayout = view.findViewById(R.id.expiryMonthInputLayout);
        expiryMonthEditText = view.findViewById(R.id.expiryMonthEditText);
        expiryYearInputLayout = view.findViewById(R.id.expiryYearInputLayout);
        expiryYearEditText = view.findViewById(R.id.expiryYearEditText);
        cvvInputLayout = view.findViewById(R.id.cvvInputLayout);
        cvvEditText = view.findViewById(R.id.cvvEditText);
        payButton = view.findViewById(R.id.payButton);
        cardTypeImageView = view.findViewById(R.id.cardTypeImageView);
        namefamInputLayout = view.findViewById(R.id.namefamInputLayout);
        namefamEditText = view.findViewById(R.id.namefamEditText);

        firebaseAuth = FirebaseAuth.getInstance();

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
                cardType = getCardTypeByPrefix(cardNumber);
                cardTypeImageView.setVisibility(View.VISIBLE);
                if (cardType.equals("Visa")) {
                    cardTypeImageView.setImageResource(R.drawable.ic_visa);
                } else if (cardType.equals("Mastercard")) {
                    cardTypeImageView.setImageResource(R.drawable.ic_mastercard);
                } else {
                    cardTypeImageView.setVisibility(View.GONE);
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
                isValidCvv(s.toString());
                updatePayButtonState();
            }
        });

        namefamEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidNameFam(s.toString());
                updatePayButtonState();
            }
        });

        payButton.setOnClickListener(v -> {
            String cardNumber = Objects.requireNonNull(cardNumberEditText.getText()).toString().replaceAll("\\s+", "");
            String expiryMonth = Objects.requireNonNull(expiryMonthEditText.getText()).toString();
            String expiryYear = Objects.requireNonNull(expiryYearEditText.getText()).toString();
            String cvv = Objects.requireNonNull(cvvEditText.getText()).toString();
            String namefam = Objects.requireNonNull(namefamEditText.getText()).toString();

            if (isValidNameFam(namefam) && isValidCardNumber(cardNumber) && isValidExpiryMonth(expiryMonth) && isValidExpiryYear(expiryYear) && isValidCvv(cvv)) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> card = new HashMap<>();
                    card.put("Name and Surname", namefam);
                    card.put("cardNumber", cardNumber);
                    card.put("cardType", cardType);
                    card.put("mm/gg", expiryMonth + "/" + expiryYear);
                    card.put("cvv", cvv);
                    db.collection("users").document(uid)
                            .collection("cards").document(cardNumber).set(card) // Используем add() для автоматического создания ID документа
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(requireContext(), "Карта добавлена", Toast.LENGTH_SHORT).show();
                                cardNumberEditText.setText("");
                                expiryMonthEditText.setText("");
                                expiryYearEditText.setText("");
                                cvvEditText.setText("");
                                namefamEditText.setText("");
                                cardTypeImageView.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Ошибка добавления карты: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                } else {
                    Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
                }
            } else {

            }
        });
        updatePayButtonState();
    }

    private String getCardTypeByPrefix(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "Visa";
        } else if (cardNumber.startsWith("5")) {
            return "Mastercard";
        }
        return "";
    }

    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.length() == 16;
    }

    private boolean isValidNameFam(String nameFam) {
        namefamInputLayout.setErrorEnabled(false);
        if (nameFam.isEmpty()) {
            namefamInputLayout.setError("Введите имя и фамилию");
            return false;
        }
        return true;
    }

    private boolean isValidExpiryMonth(String month) {
        expiryMonthInputLayout.setErrorEnabled(false);
        try {
            int m = Integer.parseInt(month);
            if (m < 1 || m > 12) {
                expiryMonthInputLayout.setError("Неверный месяц");
                return false;
            }
            expiryMonthInputLayout.setErrorEnabled(false);
            return true;
        } catch (NumberFormatException e) {
            expiryMonthInputLayout.setError("Неверный формат месяца");
            return false;
        } finally {
            if (month.isEmpty()) {
                expiryMonthInputLayout.setError("Введите месяц");
                return false;
            }
        }
    }

    private boolean isValidExpiryYear(String year) {
        if (year.isEmpty()) {
            expiryYearInputLayout.setError("Введите год");
            return false;
        }
        try {
            int y = Integer.parseInt(year);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100;
            if (y < currentYear) {
                expiryYearInputLayout.setError("Неверный год");
                return false;
            }
            expiryYearInputLayout.setErrorEnabled(false);
            return true;
        } catch (NumberFormatException e) {
            expiryYearInputLayout.setError("Неверный формат года");
            return false;
        }
    }

    private boolean isValidCvv(String cvv) {
        return cvv.length() == 3; // Пример
    }

    private void updatePayButtonState() {
        String cardNumber = Objects.requireNonNull(cardNumberEditText.getText()).toString().replaceAll("\\s+", "");
        String expiryMonth = expiryMonthEditText.getText().toString();
        String expiryYear = expiryYearEditText.getText().toString();
        String cvv = Objects.requireNonNull(cvvEditText.getText()).toString();
        String namefam = Objects.requireNonNull(namefamEditText.getText()).toString();

        boolean isCardNumberValid = isValidCardNumber(cardNumber);
        boolean isExpiryMonthValid = isValidExpiryMonth(expiryMonth);
        boolean isExpiryYearValid = isValidExpiryYear(expiryYear);
        boolean isCvvValid = isValidCvv(cvv);
        boolean isnamefamValid = isValidNameFam(namefam);

        payButton.setEnabled(isnamefamValid && isCardNumberValid && isExpiryMonthValid && isExpiryYearValid && isCvvValid);
    }
}