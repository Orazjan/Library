package com.example.library.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.library.Prevalent.Prevalent;
import com.example.library.R;
import com.example.library.UI.Activities.LogRegResForEnter.loginActivity;
import com.example.library.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private FloatingActionButton fab;
    private ImageView logoutBtn, settingBtn;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int DOUBLE_BACK_PRESS_INTERVAL = 2000;
    private ProgressBar loadingBar;
    private FirebaseAuth auth;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(HomeActivity.this, "Нажмите 'Назад' ещё раз для выхода", Toast.LENGTH_SHORT).show();

            new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, DOUBLE_BACK_PRESS_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DrawerLayout drawer = binding.drawerLayout;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_gallery)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        try {
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadingBar = findViewById(R.id.progressBar);
        logoutBtn = findViewById(R.id.logoutBtn);
        settingBtn = findViewById(R.id.settingsBtn);
        fab = findViewById(R.id.fab);
        auth = FirebaseAuth.getInstance();
        Paper.init(this);
        FirebaseUser currentUser = auth.getCurrentUser();

        if (getIntent().getBooleanExtra("fromSettings", false)) {
            if (currentUser != null) {
                fetchUsername(currentUser.getUid(), currentUser.getEmail());
            }
        } else if (getIntent().getBooleanExtra("fromlogin", false)) {
            if (currentUser != null) {
                fetchUsername(currentUser.getUid(), currentUser.getEmail());
            }
        } else {
            PaperRead();
        }

        logoutBtn.setOnClickListener(v -> {
            Paper.book().destroy();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        });

        settingBtn.setVisibility(View.INVISIBLE);
        settingBtn.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        });

        fab.setOnClickListener(V -> {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void PaperRead() {
        String userEmailKeyFromPaper = Paper.book().read(Prevalent.UserEmailKey);
        String userPasswordKeyFromPaper = Paper.book().read(Prevalent.UserPassword);
        loadingBar.setVisibility(View.VISIBLE);
        if (userEmailKeyFromPaper != null && userPasswordKeyFromPaper != null) {
            if (!TextUtils.isEmpty(userEmailKeyFromPaper) && !TextUtils.isEmpty(userPasswordKeyFromPaper)) {
                auth.signInWithEmailAndPassword(userEmailKeyFromPaper, userPasswordKeyFromPaper)
                        .addOnCompleteListener(HomeActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    if (user != null) {
                                        settingBtn.setVisibility(View.VISIBLE);
                                        loadingBar.setVisibility(View.INVISIBLE);
                                        fetchUsername(user.getUid(), user.getEmail());
                                    }
                                } else {
                                    settingBtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(HomeActivity.this, "Ошибка автовхода!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(HomeActivity.this, loginActivity.class));
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.setVisibility(View.INVISIBLE);
                                Log.e("On Login Failure", "Ошибка автовхода: " + e.getMessage());
                                Toast.makeText(HomeActivity.this, "Ошибка автовхода: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(HomeActivity.this, loginActivity.class));
                            }
                        });
            } else {
                Log.w("PaperRead", "Получены пустые email или пароль из Paper.");
                Toast.makeText(HomeActivity.this, "Не удалось получить данные для автологина.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        } else {
            Log.i("PaperRead", "Данные email или пароля не найдены в Paper.");
            Toast.makeText(HomeActivity.this, "Автоматический вход невозможен. Проверьте свои данные.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
    }

    private void fetchUsername(String uid, String email) {
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("userInfo").document(email).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException error) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            Toast.makeText(HomeActivity.this, username + ", добро пожаловать", Toast.LENGTH_SHORT).show();
                            Log.d("FirebaseData", "Username (from userInfo): " + username);
                        } else {
                            Log.d("FirebaseData", "Документ userInfo не существует");
                        }
                    }
                });
    }
}