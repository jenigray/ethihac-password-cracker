import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Jennica on 31/07/2016.
 */
public class FileController {

    private Stage primaryStage;
    private FileChooser fileChooser = new FileChooser();

    @FXML
    private ChoiceBox passwordChoiceBox;
    @FXML
    private ChoiceBox shadowChoiceBox;
    @FXML
    private ChoiceBox dictionaryChoiceBox;


    public FileController() {
        this.fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private ArrayList<String> getFilePaths() {
        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(this.passwordChoiceBox.getSelectionModel().getSelectedItem().toString());
        filePaths.add(this.shadowChoiceBox.getSelectionModel().getSelectedItem().toString());
        filePaths.add(this.dictionaryChoiceBox.getSelectionModel().getSelectedItem().toString());
        return filePaths;
    }

    @FXML
    private void handleLocatePassword(ActionEvent event) {
        File file = this.fileChooser.showOpenDialog(this.primaryStage);

        if ( file != null ) {
            if ( !passwordChoiceBox.getItems().contains(file.getAbsolutePath()) )
                passwordChoiceBox.getItems().add(file.getAbsolutePath());

            for ( int i = 0; i < passwordChoiceBox.getItems().size(); i++ ) {
                if ( passwordChoiceBox.getItems().get(i).equals(file.getAbsolutePath()) )
                    passwordChoiceBox.getSelectionModel().select(i);
            }
        }
    }

    @FXML
    private void handleLocateShadow(ActionEvent event) {
        File file = this.fileChooser.showOpenDialog(this.primaryStage);

        if ( file != null ) {
            if ( !shadowChoiceBox.getItems().contains(file.getAbsolutePath()) )
                shadowChoiceBox.getItems().add(file.getAbsolutePath());

            for ( int i = 0; i < shadowChoiceBox.getItems().size(); i++ ) {
                if ( shadowChoiceBox.getItems().get(i).equals(file.getAbsolutePath()) )
                    shadowChoiceBox.getSelectionModel().select(i);
            }
        }
    }

    @FXML
    private void handleLocateDictionary(ActionEvent event) {
        File file = this.fileChooser.showOpenDialog(this.primaryStage);

        if ( file != null ) {
            if ( !dictionaryChoiceBox.getItems().contains(file.getAbsolutePath()) )
                dictionaryChoiceBox.getItems().add(file.getAbsolutePath());

            for ( int i = 0; i < dictionaryChoiceBox.getItems().size(); i++ ) {
                if ( dictionaryChoiceBox.getItems().get(i).equals(file.getAbsolutePath()) )
                    dictionaryChoiceBox.getSelectionModel().select(i);
            }
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setTitle("Exit");
        exitAlert.setHeaderText("Are you sure you want to exit?");
        Optional<ButtonType> result = exitAlert.showAndWait();
        if ( result.get() == ButtonType.OK ) {
            System.exit(0);
        }
    }

    @FXML
    private void handleRun(ActionEvent event) {
        Cracker cracker = new Cracker();
        cracker.buildUserList(getFilePaths().get(0));
        cracker.buildCrack(getFilePaths().get(1), getFilePaths().get(2));
    }
}
