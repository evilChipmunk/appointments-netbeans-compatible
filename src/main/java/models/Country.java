package models;
 

public class Country extends BaseEntity {
    private final String name;

    public Country(String name) { 
        this.name = name;
    }

    public Country(int id, String name, AuditInfo audit) {
        super(id, audit);
        this.name = name;
    }

    public String getName() {
        return name;
    } 
}
