package models;


public class City extends BaseEntity {
    private final String name;
    private final Country country;

    public City(int id, String name, Country country, AuditInfo audit) {
        super(id, audit);
        this.name = name;
        this.country = country;

    }

    public City(String name, Country country){
        super(0, new AuditInfo());
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }
 
    public Country getCountry() {
        return country;
    }
 
}
