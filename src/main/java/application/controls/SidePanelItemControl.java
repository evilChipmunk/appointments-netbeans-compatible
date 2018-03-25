package application.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class SidePanelItemControl extends VBox  {

    private String linkText;
    private String imagePath;
    private String imageAltPath;
    private String imageCSSPath;
    private EventHandler<MouseEvent> clickAction;


    public SidePanelItemControl(){
        ScreenLoader.load("/views/SidePanelItemView.fxml", this, this);
    }

    @FXML
    public void initialize() {
        this.setOnMouseEntered(event -> {
            javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResource("/images/" + imageAltPath).toExternalForm());
            imgView.setImage(image);
        });
        this.setOnMouseExited(event -> {

            javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResource("/images/" + imagePath).toExternalForm());
            imgView.setImage(image);
        });

    }
    @FXML
    private ImageView imgView;

    @FXML
    private Label navLink;


    public String getLinkText(){
        return linkText;
    }

    public void setLinkText(String value){
        linkText = value;
        this.navLink.setText(linkText);
    }

    public String getImagePath(){
        return imagePath;
    }

    public void setImagePath(String value){
        imagePath = value;
    }

    public String getImageAltPath(){
        return imageAltPath;
    }

    public void setImageAltPath(String value){
        imageAltPath = value;
    }

    public String getImageCSSPath() {
        return imageCSSPath;
    }

    public void setImageCSSPath(String imageCSSPath) {
        this.imageCSSPath = imageCSSPath;
        this.imgView.getStyleClass().add(this.imageCSSPath);
    }

    public void setOnClickAction(EventHandler<MouseEvent> handler) {
        this.clickAction = handler;
        this.setOnMouseClicked(clickAction);
    }

    public EventHandler<MouseEvent> getOnClickAction() {
        return this.clickAction;
    }



    private void setupNodeClick(Node node, Consumer<ActionEvent> action){
        node.setOnMouseClicked(event -> {
            action.accept(null);
        });
    }


}
