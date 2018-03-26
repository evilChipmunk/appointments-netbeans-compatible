package models;
 

public class User extends BaseEntity {

    private final String name;
    private final String password;
    private final boolean active;

    public User(String name, String password, boolean active){
        super();
        this.name = name;
        this.password = password;
        this.active = active;
    }
    public User(int id, String name, String password, boolean active, AuditInfo audit) {
        super(id, audit);
        this.name = name;
        this.password = password;
        this.active = active;
    }

    public String getName() {
        return name;
    }
 
    public String getPassword() {
        return password;
    }
 

    public boolean isActive() {
        return active;
    } 
}
