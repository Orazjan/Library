package com.example.library.Model;

import java.io.Serializable;

public class showAllModel implements Serializable {
    private String name, author, img_url, type, description;
    private int price, rate;

    public void setRate(int rate) {
        this.rate = rate;
    }

    public showAllModel() {
    }

    public showAllModel(String author, String img_url, String description, String name, int price, String type, int rate) {
        this.author = author;
        this.img_url = img_url;
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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