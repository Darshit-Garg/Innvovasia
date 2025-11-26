module com.mycompany.probabilisticdist {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.probabilisticdist to javafx.fxml;
    exports com.mycompany.probabilisticdist;
    requires commons.math3;
}
