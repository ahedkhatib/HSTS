module org.group7 {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.group7.client to javafx.fxml;
    exports org.group7.client;
}
