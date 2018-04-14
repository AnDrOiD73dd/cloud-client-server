import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public VBox signup;
    public TextField loginField;
    public PasswordField passwordField;
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
        String usernamePattern = "[a-zA-Z0-9._\\-]{3,}";
        if (!username.matches(usernamePattern)) {
            Utils.showAlert("Имя пользователя должно быть не короче 3-х символов. Разрешенные символы:" +
                    "\nцифры, буквы, точка, нижнее подчеркивание, тире");
            return false;
        }
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (!password.matches(passwordPattern)) {
            Utils.showAlert("Пароль должен состоять не менее чем из 8 символов и иметь по одному символу:" +
                    "\nцыфры\nстрочной буквы\nпрописной буквы\nсодержать хотя бы один символ из: [@#$%^&+=\nи не должен содержать пробелы");
            return false;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        if (!matcher.find()) {
            Utils.showAlert("Вы указали невалидное значение в поле email");
            return false;
        }
        return true;
    }
}
