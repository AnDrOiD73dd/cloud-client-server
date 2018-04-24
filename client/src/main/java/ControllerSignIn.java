import javafx.application.Platform;
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
import protocol.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ControllerSignIn extends BaseController implements RequestHandler, ResponseHandler {

    public VBox signin;
    public TextField loginField;
    public PasswordField passwordField;
    private RequestMessage lastRequest;

    public ControllerSignIn() {
    }

    public void onClickSignIn(ActionEvent actionEvent) {
        if (isValidCredentials()) {
            if (socket == null || socket.isClosed()){
                connect(Constants.SERVER_IP, Constants.SERVER_PORT);
            }
            try {
                RequestMessage newRequest;
                do {
                    newRequest = (RequestMessage) RequestMessageFactory.getLoginMessage(MessageUtil.getId(), loginField.getText().trim(), passwordField.getText().trim());
                } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
                lastRequest = newRequest;
                out.writeUTF(lastRequest.toString());
                loginField.clear();
                passwordField.clear();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
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
            System.out.println(e.getMessage());
        }
    }

    private void showCloudClient() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("layout_cloud.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Constants.APP_NAME);
            Utils.setupIcon(stage, getClass());
            stage.setScene(new Scene(root));
            stage.show();
            // Hide this current window (if this is what you want)
            signin.getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(boolean flag) {
        loginField.setEditable(flag);
        passwordField.setEditable(flag);
    }

    @Override
    protected void obtainFile(File requestFile) {

    }

    @Override
    protected void parseCommand(String message) {
        System.out.println("parseCommand: " + message);
        MessageParser.parse(message, lastRequest, this, this);
    }

    @Override
    public void handleRequest(RequestMessage requestMessage) {

    }

    @Override
    public void handleResponse(ResponseMessage responseMessage, String command) {
        if (lastRequest.getId() == responseMessage.getId()) {
            switch (lastRequest.getCmd()) {
                case CommandList.SIGN_IN:
                    switch (responseMessage.getResponseCode()) {
                        case 0:
                            // TODO: show progress
                            updateUI(false);
                            break;
                        case 1:
                            Platform.runLater(this::showCloudClient);
                            break;
                        case 2:
                            // TODO: hide progress
                            updateUI(true);
                            Utils.showAlert("Неверый формат данных. Обратитесь к разработчику.");
                            break;
                        case 3:
                            // TODO: hide progress
                            updateUI(true);
                            Utils.showAlert("Неверный логин и/или пароль");
                            break;
                        case 4:
                            // TODO: hide progress
                            updateUI(true);
                            Utils.showAlert("Произошла внутрення ошибка на сервере");
                            break;
                        default:
                            System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                                    + ", cmd=" + CommandList.SIGN_IN);
                            break;
                    }
                    break;
                default:
                    System.out.println(("Unknown command=" + command));
                    break;
            }
        }
        else System.out.println("Unknown response id: " + responseMessage);
    }
}
