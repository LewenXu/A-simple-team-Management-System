package controller;

import au.edu.uts.ap.javafx.Controller;
import au.edu.uts.ap.javafx.ViewLoader;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.application.League;
import model.application.Manager;
import model.exception.UnauthorisedAccessException;

public class LoginController extends Controller<League> {
    @FXML private TextField idTf;

    @FXML private void handleLogin() {
        try {
            int id = Integer.parseInt(idTf.getText().trim());
            Manager m = model.validateManager(id);
            model.setLoggedInManager(m);
            ViewLoader.showStage(model, "/view/ManagerDashboardView.fxml",
                    "Manager Dashboard", new Stage());
            stage.close();
        } catch (NumberFormatException e) {
            showError("The Manager ID must be an integer.");
        } catch (UnauthorisedAccessException e) {
            showError(e.getMessage());
        }
    }
    @FXML private void handleExit() {
        stage.close();
    }
    private void showError(String message) {
        ViewLoader.showStage(message, "/view/ErrorView.fxml", "Error", new Stage());
    }
}