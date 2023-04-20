package com.dschulz.rucconv;

import com.dschulz.rucconv.controller.ConversorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("inicio.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        ConversorController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setTitle("Listado de RUCs");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
