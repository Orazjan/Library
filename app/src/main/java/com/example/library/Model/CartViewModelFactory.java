package com.example.library.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CartViewModelFactory implements ViewModelProvider.Factory {
    private static CartViewModelFactory instance;
    private final Application application;

    private CartViewModelFactory(Application application) {
        this.application = application;
    }

    public static synchronized CartViewModelFactory getInstance(Application application) {
        if (instance == null) {
            instance = new CartViewModelFactory(application);
        }
        return instance;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
