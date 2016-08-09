import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Jennica on 31/07/2016.
 */
public class FileController implements Initializable {

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
        boolean build = cracker.build(this.getFilePaths());

        TextArea resultTextArea = new TextArea();
        resultTextArea.setWrapText(true);
        resultTextArea.setEditable(false);
        resultTextArea.getStylesheets().add(FileController.class.getResource("result-text-area.css").toExternalForm());
        ScrollPane scrollPane = new ScrollPane(resultTextArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        StackPane root = new StackPane(scrollPane);
        Scene scene = new Scene(root, 600, 400);

        if (build) {
            String finalStr = "";
            for ( String result : cracker.getResults() ) {
                finalStr += result + "\n";
            }

            resultTextArea.setText(finalStr);
        } else {

        }

        Stage resultStage = new Stage();
        resultStage.setTitle("Result");
        resultStage.initModality(Modality.APPLICATION_MODAL);
        resultStage.setResizable(false);
        resultStage.centerOnScreen();
        resultStage.setScene(scene);
        resultStage.show();
    }

    private void initializeDefaultChoiceBoxes() {
        FilePathHandler readSaveFile = this.loadFilenames("save/savelist.txt");

        this.addToChoiceBoxes(this.dictionaryChoiceBox, readSaveFile.getFilePaths().get(0));
        this.addToChoiceBoxes(this.passwordChoiceBox, readSaveFile.getFilePaths().get(1));
        this.addToChoiceBoxes(this.shadowChoiceBox, readSaveFile.getFilePaths().get(2));

        this.dictionaryChoiceBox.getSelectionModel().select(this.dictionaryChoiceBox.getItems().get(readSaveFile.getSelectedIndexes().get(0)));
        this.passwordChoiceBox.getSelectionModel().select(this.passwordChoiceBox.getItems().get(readSaveFile.getSelectedIndexes().get(1)));
        this.shadowChoiceBox.getSelectionModel().select(this.shadowChoiceBox.getItems().get(readSaveFile.getSelectedIndexes().get(2)));
    }

    private void addToChoiceBoxes(ChoiceBox choiceBox, ArrayList<String> choices) {
        for ( int x = 0; x < choices.size(); x++ ) {
            choiceBox.getItems().add(choices.get(x));
        }
    }

    private FilePathHandler loadFilenames(String filename) {
        ArrayList<ArrayList<String>> completeFileNames = new ArrayList<>();
        ArrayList<String> filepathList = null;
        ArrayList<Integer> selectedIndexes = new ArrayList<>();

        try ( BufferedReader br = new BufferedReader(new FileReader(filename)) ) {
            String line = br.readLine();

            while ( line != null ) {
                boolean IsSkept = false;
                if ( line.equals("/*StartOfFile*/") ) {
                    filepathList = new ArrayList<>();
                } else if ( line.equals("/*EndOfFile*/") ) {
                    completeFileNames.add(filepathList);
                } else if ( line.equals("Dictionary File") || line.equals("Linux Password File") ||
                        line.equals("Shadow Password File") || line.equals("/*StartOfIndex*/") ||
                        line.equals("/*EndOfIndex*/") ) {
                    line = br.readLine();
                    IsSkept = true;
                } else if ( line.length() == 1 ) {
                    selectedIndexes.add(Integer.parseInt(line));
                } else {
                    filepathList.add(line);
                    System.out.println(line);
                }
                if ( !IsSkept ) {
                    line = br.readLine();
                }

            }
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return new FilePathHandler(completeFileNames, selectedIndexes);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initializeDefaultChoiceBoxes();
    }
}
