package models;
 
import java.time.ZonedDateTime;

public class AuditInfo
{
    private ZonedDateTime createDate;
    private String createdBy;
    private ZonedDateTime lastUpdate;
    private String lastUpdatedBy;

    public AuditInfo(){
        createDate = ZonedDateTime.now();
        createdBy = "system";
        lastUpdate = ZonedDateTime.now();
        lastUpdatedBy = "system";
    }
    public AuditInfo(String createdBy, ZonedDateTime createDate, String lastUpdateBy, ZonedDateTime lastUpdate) {

        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdateBy;
    }

    public void updateAudit(String userName, ZonedDateTime date){
        if ("system".equals(createdBy)){
            createdBy = userName;
            createDate = date;
        }
        lastUpdatedBy = userName;
        lastUpdate = date;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdatedBy;
    }
}
