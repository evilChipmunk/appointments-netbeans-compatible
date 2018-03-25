package models;

import java.util.UUID;

public class Country extends BaseEntity {
    private String name;

    public Country(String name) {
        super(0, new AuditInfo());
        this.name = name;
    }

    public Country(int id, String name, AuditInfo audit) {
        super(id, audit);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
