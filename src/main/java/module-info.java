module com.example.idpaproject2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.xml;

    opens com.example.idpaproject2 to javafx.fxml;
    exports com.example.idpaproject2;
}