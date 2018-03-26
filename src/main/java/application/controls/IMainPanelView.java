package application.controls;

import application.messaging.IListener; 
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;

public interface IMainPanelView{ 
    void addListener(IListener listener);
    void setContentSize(ReadOnlyObjectProperty<Bounds> bounds);
}

