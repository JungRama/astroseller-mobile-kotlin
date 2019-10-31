package com.example.mobilefinalproject.Models;

public class ProductModel{
    private String user_id;
    private String product_id;

    public String getUser_id() {
        return user_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getProduct_price() {
        return product_price;
    }

    public String getProduct_description() {
        return product_description;
    }

    private String product_name;
    private String product_image;
    private String product_price;
    private String product_description;

    public ProductModel(){
     // EMPTY CONSTRUCTOR NEEDED
    }

    public ProductModel(String user_id, String product_id, String product_name, String product_image, String product_price, String product_description) {
        this.user_id = user_id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_image = product_image;
        this.product_price = product_price;
        this.product_description = product_description;
    }

}
