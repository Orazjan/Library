package com.example.library.UI.Users;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.library.Model.Users;
import com.example.library.Prevalent.Prevalent;
import com.example.library.R;
import com.example.library.UI.Admin.AdminCategoryActiviti;
import com.example.library.UI.loginActivity;
import com.example.library.UI.regActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button regBtn, logBtn, homeBtn;
    private ProgressDialog loadingBar;


    @SuppressLint("MissingInflatedId")
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
        regBtn = (Button) findViewById(R.id.regBtn);
        logBtn = (Button) findViewById(R.id.logbtn);
        homeBtn = findViewById(R.id.glawBtn);
        loadingBar = new ProgressDialog(this);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginintent = new Intent(MainActivity.this, loginActivity.class);
                startActivity(loginintent);

            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regintent = new Intent(MainActivity.this, regActivity.class);
                startActivity(regintent);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeintent = new Intent(MainActivity.this, AdminCategoryActiviti.class);
                startActivity(homeintent);
            }
        });
        Paper.init(this);

        String userPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.UserPassword);

        if (userPhoneKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)) {
                ValidateUser(userPhoneKey, userPasswordKey);
            }
        }


    }

    private void ValidateUser(String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        String parentDbName = "Users";
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()) {
                    String userName = dataSnapshot.child(parentDbName).child(phone).child("name").getValue(String.class);
                    String userFam = dataSnapshot.child(parentDbName).child(phone).child("fam").getValue(String.class);
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    assert usersData != null;

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            if (parentDbName.equals("Admins")) {
                                loadingBar.dismiss();
                                Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                                Intent adminIntent = new Intent(MainActivity.this, AdminCategoryActiviti.class);
                                startActivity(adminIntent);
                            } else if (parentDbName.equals("Users")) {
                                loadingBar.dismiss();
                                Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);

                                homeIntent.putExtra("userName", userName.toString());
                                homeIntent.putExtra("userFam", userFam.toString());;
                                startActivity(homeIntent);
                            }

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Аккаунт с номером " + phone + "не существует", Toast.LENGTH_SHORT).show();

                    Intent registerIntent = new Intent(MainActivity.this, regActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
