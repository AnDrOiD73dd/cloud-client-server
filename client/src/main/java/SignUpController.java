import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class SignUpController {
    public VBox signup;
    public TextField loginField;
    public TextField passwordField;
    public TextField firstNameField;
    public TextField lastNameField;
    public TextField emailField;

    public void onClickSignUp(ActionEvent actionEvent) {
        if (isValidCredentials()) {

        }
    }

    private boolean isValidCredentials() {
        String username = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()){
            Utils.showAlert("Вы заполнили не все поля");
            return false;
        }
        return true;
    }
}
