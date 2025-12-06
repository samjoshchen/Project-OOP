package com.martminds.model.store;

import com.martminds.model.common.Address;

public class Store {
    private String storeId;
    private String name;
    private Address address;
    private String contactNumber;
    private double rating;
    private long totalRate;

    public Store(String storeId, String name, Address address, String contactNumber) {
        this.storeId = storeId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.rating = 0.0;
        this.totalRate = 0;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public double getRating() {
        return rating;
    }

    public long getTotalRate() {
        return totalRate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setRating(double rating) {
        if (rating >= 0.0 && rating <= 5.0) {
            this.rating = rating;
        }
    }

    public void setTotalRate(long totalRate) {
        if (totalRate >= 0) {
            this.totalRate = totalRate;
        }
    }

    public void addRating(double newRating) {
        if (newRating >= 0.0 && newRating <= 5.0) {
            this.totalRate++;
            this.rating = ((this.rating * (this.totalRate - 1)) + newRating) / this.totalRate;
        }
    }

    @Override
    public String toString() {
        return String.format("Store[ID=%s, Name=%s, Contact=%s, Rating=%.1f]",
                storeId, name, contactNumber, rating);
    }
}
