package models;


public class City extends BaseEntity {
    private String name;
    private Country country;

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

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
