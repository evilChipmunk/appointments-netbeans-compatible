package models;

import java.util.UUID;

public class User extends BaseEntity {

    private String name;
    private String password;
    private boolean active;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
