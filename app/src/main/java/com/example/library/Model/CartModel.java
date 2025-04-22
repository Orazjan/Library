package com.example.library.Model;

public class CartModel {
    String img_url, author, name;
    int price, count;

    public CartModel(String author, String name, int price, int count, String img_url) {
        this.author = author;
        this.count = count;
        this.img_url = img_url;
        this.name = name;
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public int getCount() {
        return count;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
