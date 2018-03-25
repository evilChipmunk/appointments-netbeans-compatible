package application.alerts;

import javafx.scene.control.Alert;

public class AlertFactory{

    public Alert create(AlertBuilder builder, AlertContent content){
        if (content.getAlertType() != null){
            builder.buildAlertType(content.getAlertType());
        }
        if (content.getTitle() != null){
            builder.buildTitle(content.getTitle());
        }

        //both options need a message -- if it really needs to be blanked out it can be overriden in the subclass
        if (content.getMessage() != null) {
            //do one or the other, either just a message or message and expanded content
            if (content.getLargeContent() != null) {
                builder.buildExpandableContent(content.getMessage(), content.getLargeContent());
            } else {
                builder.buildMessage(content.getMessage());
            }
        }

        builder.buildStyling();
        return builder.getAlert();
    }
}

