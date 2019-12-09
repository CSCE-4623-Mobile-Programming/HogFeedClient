package com.example.hogfeed.Model;

public class Event
{
    //ID
    private int id;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    //Latitude
    private String latitude;
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    //Location
    private String location;
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    //Longitude
    private String longitude;
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    //Quantity
    private int quantity;
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //Title
    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    //PictureID
    private String pictureid;
    public String getPictureid() {
        return pictureid;
    }
    public void setPictureid(String pictureid) {
        this.pictureid = pictureid;
    }

    //Description
    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    //Constructor
    public Event(int id, String latitude, String location, String longitude, int quantity, String title, String pictureid, String description)
    {
        this.id = id;
        this.latitude = latitude;
        this.location = location;
        this.longitude = longitude;
        this.quantity = quantity;
        this.title = title;
        this.pictureid = pictureid;
        this.description = description;
    }

    //Constructor
    public Event(String latitude, String location, String longitude, int quantity, String title, String pictureid, String description) {
        this.latitude = latitude;
        this.location = location;
        this.longitude = longitude;
        this.quantity = quantity;
        this.title = title;
        this.pictureid = pictureid;
        this.description = description;
    }

    //Constructor
    public Event()
    {

    }

    //JSON Format
    @Override
    public String toString()
    {
        return "Event{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", location=" + location +
                ", longitude=" + longitude +
                ", quantity=" + quantity +
                ", title=" + title +
                ", pictureid=" + pictureid +
                ", description=" + description +
                "}";
    }
}
