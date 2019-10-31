package com.example.mobilefinalproject.Models

class Product {
    var user_id:String?=null
    var product_id:String?=null
    var product_name:String?=null
    var product_price:String?=null
    var product_description:String?=null



    constructor(
        user_id: String?,
        product_id: String?,
        product_name: String?,
        product_price: String?,
        product_description: String?
    ) {
        this.user_id = user_id
        this.product_id = product_id
        this.product_name = product_name
        this.product_price = product_price
        this.product_description = product_description
    }




}