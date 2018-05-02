package controller;

import base.ClientUtils;
import base.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import presenter.SignUpPresenter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController extends BaseController implements Initializable {

    @FXML
    public VBox rootSignUp;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public TextField emailField;
    @FXML
    public Button signUp;
    @FXML
    public TextField serverAddress;
    @FXML
    public TextField serverPort;

    private final SignUpPresenter presenter;

    public SignUpController() {
        presenter = new SignUpPresenter(this);
    }

    public void onClickSignUp(ActionEvent actionEvent) {
        presenter.onClickSignUp(actionEvent, loginField.getText(), passwordField.getText(), firstNameField.getText(),
                lastNameField.getText(), emailField.getText(), serverAddress.getText(), serverPort.getText());
    }

    public void showCloudClient() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/layout/layout_cloud.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Constants.APP_NAME);
            ClientUtils.setupIcon(stage, getClass());
            stage.setScene(new Scene(root));
            stage.show();
            // Hide this current window (if this is what you want)
            rootSignUp.getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUI(boolean flag) {
        loginField.setEditable(flag);
        passwordField.setEditable(flag);
        firstNameField.setEditable(flag);
        lastNameField.setEditable(flag);
        emailField.setEditable(flag);
        signUp.setDisable(!flag);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serverAddress.setText(Constants.SERVER_IP);
        serverPort.setText(String.valueOf(Constants.SERVER_PORT));
    }
}
