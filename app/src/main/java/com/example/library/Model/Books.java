package com.example.library.Model;

import java.io.Serializable;

public class Books implements Serializable {
    private String name, author, img_url, type, description;
    private int id, price, rate;

    public Books() {
    }

    public Books(int Id, String author, String img_url, String description, String name, int price, String type, int rate) {
        this.id = Id;
        this.author = author;
        this.img_url = img_url;
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRate() {
        return rate;
    }
}