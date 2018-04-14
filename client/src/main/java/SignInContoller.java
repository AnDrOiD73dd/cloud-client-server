import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInContoller {

    public VBox signin;
    public TextField loginField;
    public PasswordField passwordField;

    public void onClickSignIn(ActionEvent actionEvent) {
        if (isValidCredentials()) {
//            if (socket == null || socket.isClosed()){
//                connect();
//            }
//            try {// /auth loginField pass
//                out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
//                loginField.clear();
//                passField.clear();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private boolean isValidCredentials() {
        String username = loginField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()){
            Utils.showAlert("Указаны неполные данные авторизации");
            return false;
        }
        return true;
    }

    public void onClickSignUp(ActionEvent actionEvent) {
        showSignUp(actionEvent);
    }

    private void showSignUp(Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Constants.APP_NAME + ": Регистрация");
            stage.setScene(new Scene(root));
            stage.show();
            // Hide this current window (if this is what you want)
            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
