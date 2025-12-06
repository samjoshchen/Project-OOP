package com.martminds.model.common;

import com.martminds.util.ValidationUtil;

public class Address {
    private String street;
    private String city;
    private String postalCode;
    private String district;
    private String province;
    
    public Address() {
        this.street = "";
        this.city = "";
        this.postalCode = "";
        this.district = "";
        this.province = "";
    }
    
    public Address(String street, String city, String postalCode, String district, String province) {
        this.street = street;
        this.city = city;
        setPostalCode(postalCode);
        this.district = district;
        this.province = province;
    }
    
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        if (ValidationUtil.isValidPostalCode(postalCode)) {
            this.postalCode = postalCode;
        } else {
            throw new IllegalArgumentException("Invalid postal code format. Expected 5 digits.");
        }
    }
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        
        if (street != null && !street.isEmpty()) {
            fullAddress.append(street);
        }
        
        if (district != null && !district.isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(district);
        }
        
        if (city != null && !city.isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(city);
        }
        
        if (postalCode != null && !postalCode.isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(" ");
            fullAddress.append(postalCode);
        }
        
        if (province != null && !province.isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(province);
        }
        
        return fullAddress.toString();
    }
    
    // Used for logging, storing, and displaying address info
    @Override
    public String toString() {
        return getFullAddress();
    }
    
    public boolean isComplete() {
        return street != null && !street.isEmpty() && city != null && !city.isEmpty() && postalCode != null && !postalCode.isEmpty() &&
            district != null && !district.isEmpty() && province != null && !province.isEmpty();
    }
}
