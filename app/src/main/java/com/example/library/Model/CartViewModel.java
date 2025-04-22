package com.example.library.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private final MutableLiveData<List<CartModel>> cartItems = new MutableLiveData<>(new ArrayList<>());

    public CartViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<CartModel>> getCartItems() {
        return cartItems;
    }

    public void addBookToCart(CartModel model) {
        List<CartModel> current = cartItems.getValue();
        if (current != null) {
            current.add(model);
            cartItems.setValue(current);
        }
    }

    public void removeBookFromCart(CartModel book) {
        List<CartModel> currentItems = cartItems.getValue();
        if (currentItems != null) {
            currentItems.remove(book);
            cartItems.setValue(currentItems); // Trigger LiveData update
        }
    }
}
