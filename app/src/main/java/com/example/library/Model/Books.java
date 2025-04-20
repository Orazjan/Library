package com.example.library.Model;

import java.io.Serializable;

public class Books implements Serializable {
    private String name, author, img_url, description;
    private int price, rate;

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Books() {
    }

    public Books(String author, String description, String img_url, String name, int price, int rate) {
        this.author = author;
        this.description = description;
        this.img_url = img_url;
        this.name = name;
        this.price = price;
        this.rate = rate;
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
