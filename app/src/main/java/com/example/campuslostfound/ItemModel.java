package com.example.campuslostfound;

public class ItemModel {
    private int id;
    private String type;
    private String itemName;
    private String category;
    private String location;
    private String date;
    private String description;
    private String contact;
    private String imageUri;
    private String status;
    private String firebaseId;

    private String imageBase64;

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
}