package models;

public class BaseEntity {

    private final AuditInfo audit;
    private int id;

    public BaseEntity() {
        id = 0;
        audit = new AuditInfo();
    }

    public BaseEntity(int id, AuditInfo audit) {

        this.id = id;
        this.audit = audit;
    }

    public AuditInfo getAudit() {
        return audit;
    }

    public int getId() {
        return id;
    }

    //this method is only in use because the database uses auto-increment columns for convenience
    //generating integers would cause collisions if the app was actually in use by more than one person
    //or would require an extra database call (and possible collision) to get the next id
    //self generation of id's such as guids would create their own identities without collisions.
    public void setId(int id) throws Exception {
        if (this.id != 0 && this.id != id) {
            //just a guard check on the object. Once an object has an identity (an id), it cannot be changed
            //as this is a fundamental piece of information. Changing this state would effectively change the whole
            //object itself. Instead - just create a new object with the proper id.
            throw new Exception("The identity for the object type " + this.getClass() + " with the id: " + this.id + " "
                    + "cannot be modified after it has been set.");
        }

        this.id = id;
    }
}
