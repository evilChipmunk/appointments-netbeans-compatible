package models;

public class Address extends BaseEntity {

    private final String streetOne;
    private final String streetTwo;
    private final City city;
    private final String postalCode;
    private final String phone;

    public Address(int id, String streetOne, String streetTwo, City city, String postalCode, String phone, AuditInfo audit) {
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

    public String getStreetTwo() {
        return streetTwo;
    }

    public City getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

}
