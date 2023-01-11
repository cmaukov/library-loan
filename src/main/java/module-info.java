module com.bastechpro.libraryloan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires commons.dbutils;


    opens com.bastechpro.libraryloan to javafx.fxml;
    exports com.bastechpro.libraryloan;
}