import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController extends BaseController implements Initializable {

    @FXML
    public VBox rootSignIn;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button signIn;
    @FXML
    public Button signUp;
    @FXML
    public TextField serverAddress;
    @FXML
    public TextField serverPort;

    private SignInPresenter presenter;

    public SignInController() {
        presenter = new SignInPresenter(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serverAddress.setText(Constants.SERVER_IP);
        serverPort.setText(String.valueOf(Constants.SERVER_PORT));
    }

    public void onClickSignIn(ActionEvent actionEvent) {
        presenter.onSignInClick(loginField.getText(), passwordField.getText(), serverAddress.getText(), serverPort.getText());
    }

    public void onClickSignUp(ActionEvent actionEvent) {
        presenter.onSignUpClick(actionEvent);
    }

    void showSignUp(Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("layout_sign_up.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Constants.APP_NAME + ": Регистрация");
            ClientUtils.setupIcon(stage, getClass());
            stage.setScene(new Scene(root));
            stage.show();
            // Hide this current window (if this is what you want)
            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void showCloudClient() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("layout_cloud.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Constants.APP_NAME);
            ClientUtils.setupIcon(stage, getClass());
            stage.setScene(new Scene(root));
            stage.show();
            // Hide this current window (if this is what you want)
            rootSignIn.getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateUI(boolean flag) {
        loginField.setEditable(flag);
        passwordField.setEditable(flag);
        signIn.setDisable(!flag);
        signUp.setDisable(!flag);
    }

    public void setUsername(String s) {
        loginField.setText(s);
    }

    public void setPassword(String s) {
        passwordField.setText(s);
    }
}
