package com.example.library.UI.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.Prevalent.Prevalent;
import com.example.library.R;
import com.example.library.UI.Activities.LogRegResForEnter.loginActivity;
import com.example.library.UI.Activities.LogRegResForEnter.regActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Button regBtn, logBtn, homeBtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        regBtn = findViewById(R.id.regBtn);
        logBtn = findViewById(R.id.logbtn);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);
        PaperRead();

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, loginActivity.class));
                loadingBar.dismiss();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, regActivity.class));
                loadingBar.dismiss();

            }
        });
    }

    private void PaperRead() {
        String userEmailKeyFromPaper = Paper.book().read(Prevalent.UserEmailKey);
        String userPasswordKeyFromPaper = Paper.book().read(Prevalent.UserPassword);

        loadingBar.setTitle("Вход в приложение");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);

        if (userEmailKeyFromPaper != null && userPasswordKeyFromPaper != null) {
            if (!TextUtils.isEmpty(userEmailKeyFromPaper) && !TextUtils.isEmpty(userPasswordKeyFromPaper)) {
                loadingBar.show();
                auth.signInWithEmailAndPassword(userEmailKeyFromPaper, userPasswordKeyFromPaper)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loadingBar.dismiss();
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    Toast.makeText(MainActivity.this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Ошибка входа!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Log.e("On Login Failure", "Ошибка входа: " + e.getMessage());
                                Toast.makeText(MainActivity.this, "Ошибка входа: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Log.w("PaperRead", "Получены пустые email или пароль из Paper.");
                Toast.makeText(MainActivity.this, "Не удалось получить данные для автологина.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i("PaperRead", "Данные email или пароля не найдены в Paper.");
//            Toast.makeText(MainActivity.this, "Автоматический вход невозможен. Проверьте свои данные.", Toast.LENGTH_SHORT).show();
        }
    }
}
