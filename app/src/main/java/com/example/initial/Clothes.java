package com.example.initial;

//public class Clothes {
//    private String id; // Document ID
//    private String name;
//    private String category;
//    private String imageUrl;
//    private boolean inLaundry;
//
//    public Clothes() {
//        // Default constructor required for Firestore deserialization
//    }
//
//    public Clothes(String id, String name, String category, String imageUrl, boolean inLaundry) {
//        this.id = id;
//        this.name = name;
//        this.category = category;
//        this.imageUrl = imageUrl;
//        this.inLaundry = inLaundry;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public boolean isInLaundry() {
//        return inLaundry;
//    }
//
//    public void setInLaundry(boolean inLaundry) {
//        this.inLaundry = inLaundry;
//    }
//}

public class Clothes {
    private String id;
    private String name;
    private String category;
    private String imageUrl;
    private boolean inLaundry;
    private boolean selected; // For laundry selection

    public Clothes() {
        // Default constructor required for calls to DataSnapshot.getValue(Clothes.class)
    }

    public Clothes(String id, String name, String category, String imageUrl, boolean inLaundry) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.inLaundry = inLaundry;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isInLaundry() {
        return inLaundry;
    }

    public void setInLaundry(boolean inLaundry) {
        this.inLaundry = inLaundry;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
