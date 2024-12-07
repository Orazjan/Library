package com.example.library.UI.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.Prevalent.Prevalent;
import com.example.library.R;
import com.example.library.UI.loginActivity;
import com.example.library.UI.regActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
        regBtn = (Button) findViewById(R.id.regBtn);
        logBtn = (Button) findViewById(R.id.logbtn);
        loadingBar = new ProgressDialog(this);



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
        Paper.init(this);

        String UserEmailKey = Paper.book().read(Prevalent.UserEmailKey);
        String userPasswordKey = Paper.book().read(Prevalent.UserPassword);

        if (UserEmailKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(UserEmailKey) && !TextUtils.isEmpty(userPasswordKey)) {
                auth.signInWithEmailAndPassword(UserEmailKey, userPasswordKey).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingBar.setTitle("Вход в приложение");
                        loadingBar.setMessage("Пожалуйста, подождите...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        Toast.makeText(MainActivity.this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Log.d("On Login", "onComplete: " + e.getMessage());
                        Toast.makeText(MainActivity.this, "Ошибка входа!", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }
    }


}