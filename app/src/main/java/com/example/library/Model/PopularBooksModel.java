package com.example.library.Model;

import java.io.Serializable;

public class PopularBooksModel implements Serializable {
    private String name, author, img_url, description;
    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public PopularBooksModel() {
    }

    public PopularBooksModel(String author, String description, String img_url, String name, int price) {
        this.author = author;
        this.description = description;
        this.img_url = img_url;
        this.name = name;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        if (name.length() >= 20) {
            name = name.substring(0, 20);
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
