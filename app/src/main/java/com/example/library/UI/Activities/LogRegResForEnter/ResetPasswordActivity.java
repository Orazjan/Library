package com.example.library.UI.Activities.LogRegResForEnter;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Button btnResetPassword;
    private TextInputEditText editText;
    private TextInputLayout editMailInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnResetPassword = findViewById(R.id.btnResetPassword);
        editText = findViewById(R.id.editMailEditText);
        editMailInputLayout = findViewById(R.id.editMailInputLayout);
        auth = FirebaseAuth.getInstance();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ValidMail(editable.toString());
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(editText.getText()).toString();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(ResetPasswordActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                makeText(ResetPasswordActivity.this, "Проверьте почту", LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, loginActivity.class));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeText(ResetPasswordActivity.this, "Ошибка", LENGTH_SHORT).show();
                            Log.d("On Reset Password", "onComplete: " + e.getMessage());
                        }
                    });
                }
        });
    }

    private void ValidMail(String string) {
        if (TextUtils.isEmpty(string)) {
            editMailInputLayout.setError("Введите почту");
            btnResetPassword.setEnabled(false);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
            editMailInputLayout.setError("Введите корректную почту");
            btnResetPassword.setEnabled(false);
        } else {
            editMailInputLayout.setError(null);
            btnResetPassword.setEnabled(true);
        }
    }
}