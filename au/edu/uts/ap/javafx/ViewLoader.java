package au.edu.uts.ap.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ViewLoader {

    private static <T> void loadStage(T model, String fxml, String title, Stage stage,
                                      Runnable onStageClosed) throws IOException {
        URL location = ViewLoader.class.getResource(fxml);
        if (location == null) {
            throw new IOException("FXML resource not found: " + fxml);
        }

        FXMLLoader loader = new FXMLLoader(location, null, null,
                type -> {
                    try {
                        @SuppressWarnings("unchecked")
                        Controller<T> controller = (Controller<T>) type.getDeclaredConstructor().newInstance();
                        controller.model = model;
                        controller.stage = stage;
                        return controller;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        Parent root = loader.load();
        if (onStageClosed != null) {
            stage.setOnHidden(event -> onStageClosed.run());
        }

        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    public static <T> void showStage(T model, String fxml, String title, Stage stage) {
        showStage(model, fxml, title, stage, null);
    }

    public static <T> void showStage(T model, String fxml, String title, Stage stage,
                                     Runnable onStageClosed) {
        try {
            loadStage(model, fxml, title, stage, onStageClosed);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load view: " + fxml, e);
        }
    }
}
