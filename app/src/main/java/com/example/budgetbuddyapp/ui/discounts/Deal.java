package com.example.budgetbuddyapp.ui.discounts;

import java.text.DecimalFormat;

public class Deal {

//Example JSON data:
//    "id": 2158623,
//            "title": "Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case",
//            "short_title": "Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case",
//            "description": "Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Protective Shockproof Hybrid Hard Case Cover For Samsung Galaxy Note 8 / Note 9 / Note 10 / Note 10 Plus NO BUILT-IN SCREEN PROTECTOR Protection from light drops, short falls, light rain, and other everyday nuisances Port covers keep dirt and debris from getting into internal device components Dual layer design consists of inner plastic layer and shockproof exterior layer Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 8 Black Black Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 9 Black Black Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 8 Black Red Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 9 Black Red Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 8 Black Blue Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 9 Black Blue Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 8 Navy Blue Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 9 Navy Blue Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 8 Grey Teal Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 9 Grey Teal Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 8 Purple Teal Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 9 Purple Teal Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 8 Teal Pink Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 9 Teal Pink Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 8 Blue Black Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 9 Blue Black Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Black Black Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.5 inches (H) x 3.25 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Black Red Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.5 inches (H) x 3.25 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Navy Blue Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.5 inches (H) x 3.25 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Grey Teal Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.5 inches (H) x 3.25 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Purple Teal Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.5 inches (H) x 3.25 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Teal Pink Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.5 inches (H) x 3.25 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Clear Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.5 inches (H) x 3.25 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Plus Black Black Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Plus Black Red Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Plus Navy Blue Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Plus Grey Teal Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Plus Purple Teal Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Plus Teal Pink Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Samsung Galaxy Note 8 / 9 / 10 / Plus Protective Shockproof Hybrid Defender Case Samsung Galaxy Note 10 Plus Clear Condition: New Consumer Electronics Cases Materials: TPU Exterior and Plastic Interior Dimensions: 6.88 inches (H) x 3.5 inches (W) x 0.63 inches (L) Weight: 3.0 ounces Included in the box: 1 - Defender Hybrid Case Made in China",
//            "fine_print": "This item is sold through the FORTEM STORE operated by LS Consulting Enterprises INC. The merchant is solely responsible to purchasers for the fulfillment, delivery, care, quality, and pricing information of the advertised goods and services. The product is new. Product eligible for free returns within 30 days. Shipping and handling charges will be Free. United States (excluding Alaska & Hawaii) Shipments only. Does not ship to PO boxes. Orders are typically delivered in 5-10 business days.",
//            "number_sold": 0,
//            "url": "https://api.discountapi.com/v2/deals/2158623/click?api_key=kLZeIsES",
//            "price": 8.99,
//            "value": 29.99,
//            "discount_amount": 21.0,
//            "discount_percentage": 0.7002334111370457,
//            "provider_name": "Groupon",
//            "provider_slug": "groupon",
//            "category_name": "Electronics",
//            "category_slug": "electronics",
//            "image_url": "https://api.discountapi.com/v2/deals/2158623/image?api_key=kLZeIsES",
//            "online": true,
//            "expires_at": "2999-01-01T00:00:00Z",
//            "created_at": "2020-07-27T08:40:31Z",
//            "updated_at": "2020-09-18T08:40:35Z",
//            "merchant": {
//        "id": 291470,
//                "name": "FORTEM STORE",
//                "address": "",
//                "region": "",
//                "country": ""
//    }

//Deal will use the title, short_title, description, url, price, value, discount_amount, image_url, latitude, and longitude values from a JSON object.
//This is an arbitrary choice of values and may change as we figure out exactly what information we want to show the user.

    private int id;
    private String title;
    private  String short_title;
    private String description;
    private String url;
    private double price;
    private double value;
    private double discount_amount;
    private String image_url;
    private String latitude;
    private String longitude;
    private final DecimalFormat df = new DecimalFormat("#.00"); //to make the number values "look pretty"


    public Deal(int id, String title, String short_title, String description, String url, double price, double value, double discount_amount, String image_url, String latitude, String longitude) {
        this.id = id;
        this.title = title;
        this.short_title = short_title;
        this.description = description;
        this.url = url;
        this.price = price;
        this.value = value;
        this.discount_amount = discount_amount;
        this.image_url = image_url;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Deal() {
    }

    @Override
    public String toString() {
        return
                short_title + "\n    " +
                        "price = $" + df.format(price)  + "\n    " +
                        "value = $" + df.format(value) + "\n    " +
                        "discount amount = $" + df.format(discount_amount) + "\n    " +
                        "latitude = " + latitude + "\n    " +
                        "longitude = " + longitude;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShort_title() {
        return short_title;
    }

    public void setShort_title(String short_title) {
        this.short_title = short_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

}


