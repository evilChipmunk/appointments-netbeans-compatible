package models;

public class Customer extends BaseEntity {

    private final int addressId;
    private String name;
    private Address address;
    private boolean active;

    public Customer(int id, String customerName, Address address, boolean active, AuditInfo audit) {
        super(id, audit);

        if (address == null) {
            throw new IllegalArgumentException("Customer Address cannot be null.");
        }
        this.name = customerName;
        this.address = address;
        this.addressId = address.getId();
        this.active = active;
    }

    public Customer(int id, String customerName, int addressId, boolean isActive, AuditInfo audit) {
        super(id, audit);
        this.name = customerName;
        this.addressId = addressId;
        this.active = isActive;

    }

    public Customer(String customerName, Address address, Boolean isActive) {

        if (address == null) {
            throw new IllegalArgumentException("Customer Address cannot be null.");
        }

        this.name = customerName;
        this.addressId = address.getId();
        this.address = address;
        this.active = isActive;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public int getAddressId() {
        return addressId;
    }

    public boolean isActive() {
        return active;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
