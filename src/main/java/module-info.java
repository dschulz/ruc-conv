module com.dschulz.rucconv {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    //requires org.apache.commons.csv;
    requires org.controlsfx.controls;

    opens com.dschulz.rucconv to javafx.fxml;
    exports com.dschulz.rucconv;
    exports com.dschulz.rucconv.controller;
    opens com.dschulz.rucconv.controller to javafx.fxml, org.controlsfx.controls;
    opens com.dschulz.rucconv.model to javafx.base, javafx.controls, org.controlsfx.controls;
}
