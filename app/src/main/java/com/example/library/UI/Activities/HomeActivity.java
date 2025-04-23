package com.example.library.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.library.R;
import com.example.library.UI.Activities.LogRegResForEnter.loginActivity;
import com.example.library.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private FloatingActionButton fab;
    private ImageView logoutBtn, settingBtn;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int DOUBLE_BACK_PRESS_INTERVAL = 2000; // Миллисекунды между нажатиями

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
                R.id.nav_gallery,
                R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);

        try {
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logoutBtn = findViewById(R.id.logoutBtn);
        settingBtn = findViewById(R.id.settingsBtn);
        fab = findViewById(R.id.fab);

        logoutBtn.setOnClickListener(v -> {
            Paper.init(this);
            Paper.book().destroy();
            startActivity(new Intent(HomeActivity.this, loginActivity.class));
            finish();
        });

        settingBtn.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            finish();
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
}