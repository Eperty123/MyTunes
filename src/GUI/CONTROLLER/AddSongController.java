package GUI.CONTROLLER;

import BE.Song;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AddSongController extends Component implements Initializable {
    @FXML
    TextField filePathTextField;
    @FXML
    TextField titleTextField;
    @FXML
    TextField artistTextField;
    @FXML
    ComboBox genreComboBox;
    @FXML
    Button addButton;

    private MainViewController mainViewController;
    private Song songToAdd;
    private Map<Integer, String> genres;
    private String selectedCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedCategory();
    }

    /**
     * Initialize the combo box to listen to when a new item is selected.
     */
    private void selectedCategory() {
        genreComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            selectedCategory = (String) newValue;
            System.out.println("Selected category: " + selectedCategory);
        }));
    }

    /**
     * Assign the genre combo box to have the specfied hash map genres.
     *
     * @param genres The genres to add.
     */
    public void setGenreComboBox(Map<Integer, String> genres) {
        this.genres = new HashMap<>(genres);
        genreComboBox.getItems().clear();
        genreComboBox.getItems().addAll(genres.values());
    }

    /**
     * Sets the maing view controller
     *
     * @param mainViewController
     */
    public void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void browse() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                titleTextField.setText(selectedFile.getName());
                filePathTextField.setText(selectedFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Find the specfied category name and return its id.
     *
     * @param categoryName The category name to find.
     * @return
     */
    private int getCategoryIdFromName(String categoryName) {
        for (var category : genres.entrySet()) {
            if (category.getValue() == categoryName) {
                return category.getKey();
            }
        }
        return -1;
    }

    /**
     * Add the new song to database.
     */
    public void addSong() {
        try {
            songToAdd = new Song(titleTextField.getText(),filePathTextField.getText());
            songToAdd.setArtist(artistTextField.getText());
            songToAdd.setCategoryId(getCategoryIdFromName(selectedCategory));
            mainViewController.createSong(songToAdd);
            mainViewController.reloadSongTable();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        mainViewController.getWindowStage().close();
    }
}
