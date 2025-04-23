package com.example.library.Model;

public class CartItem {
    private Books book;
    private int quantity;
    private String documentId;

    public CartItem() {
    }

    public CartItem(Books book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setBook(Books book) {
        this.book = book;
    }

    public Books getBook() {
        return book;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}