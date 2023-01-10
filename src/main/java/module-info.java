module com.bastechpro.libraryloan {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.bastechpro.libraryloan to javafx.fxml;
    exports com.bastechpro.libraryloan;
}