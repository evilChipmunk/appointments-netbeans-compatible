package models;

import java.util.UUID;

public class Customer extends BaseEntity {
    private final int addressId;
    private String name;
    private Address address;
    private boolean active;

    public Customer(int id, String customerName, Address address, boolean active, AuditInfo audit) {
        super(id, audit);

        if (address == null){
            throw new IllegalArgumentException("Customer Address cannot be null.");
        }
        this.name = customerName;
        this.address = address;
        this.addressId = address.getId();
        this.active = active;
    }

    public Customer(int id, String customerName, int addressId, boolean active, AuditInfo audit) {
        super(id, audit);
        this.name = customerName;
        this.addressId = addressId;
        this.active = active;

    }

    public Customer(String customerName, Address address, Boolean isActive) {
        super(0, new AuditInfo());

        if (address == null){
            throw new IllegalArgumentException("Customer Address cannot be null.");
        }

        this.name = customerName;
        this.addressId = address.getId();
        this.address = address;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
