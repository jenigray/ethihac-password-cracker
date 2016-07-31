import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by Jennica on 31/07/2016.
 */
public class pwdcrckr extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load file choose layout from fxml file
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(pwdcrckr.class.getResource("/file_chooser.fxml"));
        Parent fileChooserView = (BorderPane) fxmlLoader.load();

        primaryStage.setScene(new Scene(fileChooserView));
        primaryStage.setTitle("Linux Password Cracker");
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();

        FileController fileController = fxmlLoader.getController();
        fileController.setPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
