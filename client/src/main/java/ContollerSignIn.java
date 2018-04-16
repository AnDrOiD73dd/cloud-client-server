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

public class ContollerSignIn {

    public VBox signin;
    public TextField loginField;
    public PasswordField passwordField;

    public void onClickSignIn(ActionEvent actionEvent) {
        if (isValidCredentials()) {
//            if (socket == null || socket.isClosed()){
//                connect();
//            }
//            try {// /auth loginField pass
//                out.writeUTF("/auth " + loginField.getText().trim() + " " + passwordField.getText().trim());
//                loginField.clear();
//                passwordField.clear();
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
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
            e.printStackTrace();
        }
    }
}
