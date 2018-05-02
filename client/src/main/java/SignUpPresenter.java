import javafx.application.Platform;
import javafx.event.ActionEvent;
import model.TransferringFile;
import protocol.*;
import protocol.handler.ResponseHandler;
import protocol.request.RequestMessage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpPresenter extends BasePresenter implements ResponseListener, ResponseHandler {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private SignUpController controller;
    private ConnectionService connectionService;
    private RequestMessage lastRequest;
    private ConnectionStateListener connectionStateListener;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;

    public SignUpPresenter(SignUpController controller) {
        this.controller = controller;
        connectionStateListener = new ConnectionStateListener() {
            @Override
            public void onConnected() {
                signUp();
            }

            @Override
            public void onDisconnected() {
                controller.updateUI(true);
            }

            @Override
            public void onError(String error) {
                controller.showAlert(error);
                controller.updateUI(true);
            }
        };
    }

    private void signUp() {
        try {
            RequestMessage newRequest;
            do {
                newRequest = (RequestMessage) RequestMessageFactory.getSignUpMessage(MessageUtil.getId(), username, password, firstName, lastName, email);
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            lastRequest = newRequest;
            connectionService.getOut().writeObject(lastRequest.toString());
//            connectionService.getOut().flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            controller.showAlert("Произошла ошибка при отправке данных на сервер");
        }
    }

    public void onClickSignUp(ActionEvent actionEvent, String username, String password, String firstName, String lastName, String email, String serverAddress, String serverPort) {
        serverAddress = serverAddress.trim();
        serverPort = serverPort.trim();
        if (!isValidServerAddress(serverAddress, serverPort)) {
            controller.showAlert("Указаны невалидные данные сервера");
            return;
        }
        if (isValidCredentials(username, password, firstName, lastName, email)) {
            initConnection(serverAddress, Integer.valueOf(serverPort));
        }
    }

    private boolean isValidCredentials(String username, String password, String firstName, String lastName, String email) {
        this.username = username.trim();
        this.password = password.trim();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email.trim();
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()){
            controller.showAlert("Вы заполнили не все поля");
            return false;
        }
        String usernamePattern = "[a-zA-Z0-9._\\-]{3,}";
        if (!username.matches(usernamePattern)) {
            controller.showAlert("Имя пользователя должно быть не короче 3-х символов. Разрешенные символы:" +
                    "\nцифры, буквы, точка, нижнее подчеркивание, тире");
            return false;
        }
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (!password.matches(passwordPattern)) {
            controller.showAlert("Пароль должен состоять не менее чем из 8 символов и иметь по одному символу:" +
                    "\nцыфры\nстрочной буквы\nпрописной буквы\nсодержать хотя бы один символ из: [@#$%^&+=\nи не должен содержать пробелы");
            return false;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        if (!matcher.find()) {
            controller.showAlert("Вы указали невалидное значение в поле email");
            return false;
        }
        return true;
    }

    protected void initConnection(String host, Integer port) {
        connectionService = ConnectionService.getInstance();
        connectionService.setResponseListener(this);
        connectionService.addConnectionStateListener(connectionStateListener);
        if (connectionService.isConnected())
            signUp();
        else connectionService.connect(host, port);
    }

    @Override
    public void onNewFile(TransferringFile file) {

    }

    @Override
    public void onNewMessage(String message) {
        System.out.println("parseCommand: " + message);
        MessageParser.parse(message, lastRequest, null, this, null);
    }

    @Override
    public void handleResponse(ResponseMessage responseMessage, String command) {
        if (lastRequest.getId() != responseMessage.getId()) {
            System.out.println("Unknown response id: " + responseMessage);
            return;
        }
        switch (lastRequest.getCmd()) {
            case CommandList.SIGN_UP:
                switch (responseMessage.getResponseCode()) {
                    case 0:
                        // TODO: show progress
                        controller.updateUI(false);
                        break;
                    case 1:
                        connectionService.removeConnectionStateListener(connectionStateListener);
                        Platform.runLater(() -> controller.showCloudClient());
                        break;
                    case 2:
                        // TODO: hide progress
                        controller.updateUI(true);
                        ClientUtils.showAlert("Неверый формат данных. Обратитесь к разработчику.");
                        break;
                    case 3:
                        // TODO: hide progress
                        controller.updateUI(true);
                        controller.showAlert("Произошла внутрення ошибка на сервере");
                        break;
                    case 4:
                        // TODO: hide progress
                        controller.updateUI(true);
                        controller.showAlert("Пользователь с таким логином уже существует");
                        break;
                    case 5:
                        // TODO: hide progress
                        controller.updateUI(true);
                        controller.showAlert("Пользователь с таким e-mail уже существует");
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
}
