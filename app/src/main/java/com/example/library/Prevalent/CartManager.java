package com.example.library.Prevalent;

import com.example.library.Model.Books;
import com.example.library.Model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Books book, int quantity) {
        // Проверяем, есть ли уже такая книга в корзине
        for (CartItem item : cartItems) {
            if (item.getBook().getId() == (book.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        // Если книги нет в корзине, добавляем новую запись
        cartItems.add(new CartItem(book, quantity));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }

    // Другие методы по необходимости (удаление, изменение количества и т.д.)
}