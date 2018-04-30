import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController extends BaseController {

    private final SignUpPresenter presenter;

    public VBox rootSignUp;
    public TextField loginField;
    public PasswordField passwordField;
    public TextField firstNameField;
    public TextField lastNameField;
    public TextField emailField;
    public Button signUp;

    public SignUpController() {
        presenter = new SignUpPresenter(this);
    }

    public void onClickSignUp(ActionEvent actionEvent) {
        presenter.onClickSignUp(actionEvent, loginField.getText(), passwordField.getText(), firstNameField.getText(),
                lastNameField.getText(), emailField.getText());
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
            rootSignUp.getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateUI(boolean flag) {
        loginField.setEditable(flag);
        passwordField.setEditable(flag);
        firstNameField.setEditable(flag);
        lastNameField.setEditable(flag);
        emailField.setEditable(flag);
        signUp.setDisable(!flag);
    }
}
