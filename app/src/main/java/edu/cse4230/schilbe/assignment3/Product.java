package edu.cse4230.schilbe.assignment3;

import android.net.Uri;

/**
 * Created by packers on 11/5/17.
 */

public class Product {
    private String name = null;
    private String price = null;
    private int image = -1;
    private Uri imageUri;


    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUris) {
        this.imageUri = imageUris;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Product(String name, String price, int image) {

        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Product(String name, String price, Uri image) {

        this.name = name;
        this.price = price;
        this.imageUri = image;
    }
}

