package application.controls;

import application.messaging.IListener;
import javafx.beans.property.ReadOnlyDoubleProperty;

import java.awt.event.ActionListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;

public interface IMainPanelView{
    ReadOnlyDoubleProperty heightProperty();
    ReadOnlyDoubleProperty widthProperty();
    void addListener(IListener listener);
    void setContentSize(ReadOnlyObjectProperty<Bounds> bounds);
}

