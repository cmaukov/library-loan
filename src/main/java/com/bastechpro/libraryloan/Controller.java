package com.bastechpro.libraryloan;
/* library-loan
 * @created 01/10/2023
 * @author Konstantin Staykov
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
    @FXML
    private ChoiceBox<BookSearchType> choiceBox;

    @FXML
    private ListView<Book> listView;

    private Library model;

    public Controller(Library model, Stage stage) {
        this.model = model;
        stage.setOnCloseRequest(e -> model.close());
    }

    public void initialize() {
        choiceBox.getItems().setAll(BookSearchType.values());
        choiceBox.getSelectionModel().selectFirst();
        listView.getItems().setAll(model.loadAll());
    }

    public void onSearch(ActionEvent event) {
        String param = ((TextField)event.getSource()).getText();

        listView.getItems().setAll(model.search(choiceBox.getValue(), param));
    }

    public void onLoan() {
        model.loanBook(listView.getSelectionModel().getSelectedItem().getUniqueID());
    }

    public void onReturn() {
        model.returnBook(listView.getSelectionModel().getSelectedItem().getUniqueID());
    }
}
