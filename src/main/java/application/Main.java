package application;

import application.controllers.MainController;
import application.controls.screens.ErrorControl;
import application.dependencyInjection.ServiceLocator;
import exceptions.ValidationException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import static java.lang.Math.random;
import java.util.Arrays;
import javafx.scene.paint.RadialGradient;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Thread.setDefaultUncaughtExceptionHandler(Main::showError);
        primaryStage.setTitle("Scheduler");
        primaryStage.centerOnScreen();

        Scene primaryScene = getPrimaryScene(primaryStage);

        Timeline timeline = new Timeline();

        timeline.setOnFinished((ActionEvent event) -> {
            primaryStage.setScene(null);
            primaryStage.setScene(primaryScene);
            primaryStage.show();
        });
        Scene splashScreen = getSplash(timeline);
        primaryStage.setScene(splashScreen);

        primaryStage.show();

    }

    private Scene getPrimaryScene(Stage primaryStage) throws java.io.IOException {

        ServiceLocator injector = ServiceLocator.getInstance();

        MainController controller = injector.Resolve(MainController.class);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/MainView.fxml"));
        fxmlLoader.setController(controller);

        Scene primaryScene = new Scene(fxmlLoader.load(), 600, 400);
        controller.setStage(primaryStage);

        primaryStage.getIcons().add(new Image(MainController.class.getResourceAsStream("/images/icons/address-card.png")));

        return primaryScene;
    }

    private Scene getSplash(Timeline timeline) {
        Group root = new Group();
         
        Scene scene = new Scene(root, 600, 400, Color.BLACK);
 
        Group circles = new Group();
        for (int i = 0; i < 30; i++) {
            Circle circle = new Circle(150, Color.web("white", 0.05));
            circle.setStrokeType(StrokeType.OUTSIDE);
            circle.setStroke(Color.web("white", 0.16));
            circle.setStrokeWidth(4);
            circles.getChildren().add(circle);
        }
        Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
                new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.web("#f8bd55")),
            new Stop(0.14, Color.web("#c0fe56")),
            new Stop(0.28, Color.web("#5dfbc1")),
            new Stop(0.43, Color.web("#64c2f8")),
            new Stop(0.57, Color.web("#be4af7")),
            new Stop(0.71, Color.web("#ed5fc2")),
            new Stop(0.85, Color.web("#ef504c")),
            new Stop(1, Color.web("#f2660f")),}));
        Group blendModeGroup
                = new Group(new Group(new Rectangle(scene.getWidth(), scene.getHeight(),
                        Color.BLACK), circles), colors);
        colors.setBlendMode(BlendMode.OVERLAY);
        root.getChildren().add(blendModeGroup);
        circles.setEffect(new BoxBlur(10, 10, 3));

        circles.getChildren().forEach((circle) -> {
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, // set start position at 0
                            new KeyValue(circle.translateXProperty(), random() * 800),
                            new KeyValue(circle.translateYProperty(), random() * 600)),
                    new KeyFrame(new Duration(2000), // set end position at 40s
                            new KeyValue(circle.translateXProperty(), random() * 800),
                            new KeyValue(circle.translateYProperty(), random() * 600)));
        });
        // play 40s of animation
        timeline.play();
        javafx.event.EventHandler<ActionEvent> onStopped = timeline.getOnFinished();

        return scene;
    }

    public static void main(String[] args) {

        launch(args);
    }

    private static void showError(Thread t, Throwable e) {
        System.err.println("***Default exception handler***");

        if (Platform.isFxApplicationThread()) {
            showErrorDialog(e);
        } else {
            System.err.println("An unexpected error occurred in " + t);
        }
    }

    private static String unPackError(Throwable e) {
        String error = "";
        Throwable ex = e;
        while (ex != null) {
            error += ex.getMessage() + "\n";
            ex = ex.getCause();
        }
        return error;
    }

    private static void showErrorDialog(Throwable e) {

        String errorMsg;
        try {
            InvocationTargetException ex = (InvocationTargetException) e.getCause();

            if (ex.getTargetException() instanceof ValidationException) {
                errorMsg = ex.getTargetException().getMessage();
                ErrorControl control = new ErrorControl(errorMsg);
                control.show();
                return;
            } else {
                errorMsg = ex.getMessage();
                errorMsg += "\r\n" + Arrays.toString(e.getStackTrace());
            }
        } catch (Exception exx) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));

            errorMsg = unPackError(e);
            errorMsg += "\r\n" + Arrays.toString(e.getStackTrace());
        }

        if (errorMsg.isEmpty()) {
            errorMsg = "Error occurred";
        }

        ErrorControl control = new ErrorControl(errorMsg);
        control.show();

    }
}
