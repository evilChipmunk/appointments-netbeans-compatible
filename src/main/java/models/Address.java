package models;

import java.util.UUID;

public class Address extends BaseEntity {

    private String streetOne;
    private String streetTwo;
    private City city;
    private String postalCode;
    private String phone;

    public Address(int id, String streetOne, String streetTwo, City city, String postalCode, String phone, AuditInfo audit)
    {
        super(id, audit);
        this.streetOne = streetOne;
        this.streetTwo = streetTwo;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    public Address(String streetOne, String streetTwo, City city, String postalCode, String phone) {
        this.streetOne = streetOne;
        this.streetTwo = streetTwo;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    public String getStreetOne() {
        return streetOne;
    }

    public void setStreetOne(String streetOne) {
        this.streetOne = streetOne;
    }

    public String getStreetTwo() {
        return streetTwo;
    }

    public void setStreetTwo(String streetTwo) {
        this.streetTwo = streetTwo;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
