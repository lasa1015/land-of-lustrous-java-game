module org.example.landoflustrous {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens org.example.landoflustrous to javafx.fxml;
    exports org.example.landoflustrous;
    exports org.example.landoflustrous.controller;
    opens org.example.landoflustrous.controller to javafx.fxml;

    exports org.example.landoflustrous.util to javafx.graphics;
    exports org.example.landoflustrous.view to javafx.graphics;
}