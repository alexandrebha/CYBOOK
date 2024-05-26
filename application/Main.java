package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 * Main class to launch the JavaFX application.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * 
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            // Adding stylesheet to the scene
            scene.getStylesheets().add(getClass().getResource("../application/style.css").toExternalForm());

            // Setting the stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Books Management");
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch(Exception e) {
            // Handling exceptions
            e.printStackTrace();
        }
    }

    /**
     * The main method to launch the application.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
