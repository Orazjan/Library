package com.example.library.Model;

public class CartItem {
    private Books book;
    private int quantity;

    public CartItem(Books book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    // Геттеры и сеттеры
    public Books getBook() {
        return book;
    }

    public void setBook(Books book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}