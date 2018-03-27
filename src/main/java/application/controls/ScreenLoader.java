package application.controls;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenLoader {

    public static void load(String path, Object root, Object controller) {

        FXMLLoader fxmlLoader = new FXMLLoader(root.getClass().getResource(path));
        fxmlLoader.setRoot(root);
        fxmlLoader.setController(controller);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static Stage loadNewStage(String path, Object root, Object controller, String title)  {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setRoot(root);
        fxmlLoader.setLocation(root.getClass().getResource(path));
        fxmlLoader.setController(controller);

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
        
        return stage;
    }

    public static Stage loadNewStage(Parent root, double width, double height, Modality modaility )
    {
        Scene scene  = new Scene(root, width, height);

        Stage stage = new Stage();

        stage.initModality(modaility);
        stage.setScene(scene);
        stage.show();
        
        return stage;

    }
}
