import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController extends BaseController {

    public VBox rootSignIn;
    public TextField loginField;
    public PasswordField passwordField;
    public Button signIn;
    public Button signUp;
    private SignInPresenter presenter;

    public SignInController() {
        presenter = new SignInPresenter(this);
    }

    public void onClickSignIn(ActionEvent actionEvent) {
        presenter.onSignInClick(loginField.getText(), passwordField.getText());
    }

    public void onClickSignUp(ActionEvent actionEvent) {
        presenter.onSignUpClick(actionEvent);
    }

    void showSignUp(Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("layout_sign_up.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Constants.APP_NAME + ": Регистрация");
            Utils.setupIcon(stage, getClass());
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
            Utils.setupIcon(stage, getClass());
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