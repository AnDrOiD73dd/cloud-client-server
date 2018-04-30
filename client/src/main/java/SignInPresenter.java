import javafx.event.Event;
import protocol.*;

import java.io.File;
import java.io.IOException;

public class SignInPresenter implements ResponseListener, RequestHandler, ResponseHandler {

    private SignInController controller;
    private ConnectionService connectionService;
    private RequestMessage lastRequest;
    private ConnectionStateListener connectionStateListener;
    private String username;
    private String password;

    public SignInPresenter(SignInController controller) {
        this.controller = controller;
        connectionStateListener = new ConnectionStateListener() {
            @Override
            public void onConnected() {
                signIn();
            }

            @Override
            public void onDisconnected() {
//                controller.showAlert("Подключение с сервером разорвано");
            }

            @Override
            public void onError(String error) {
                controller.showAlert(error);
            }
        };
    }

    private void signIn() {
        try {
            RequestMessage newRequest;
            do {
                newRequest = (RequestMessage) RequestMessageFactory.getLoginMessage(MessageUtil.getId(), username, password);
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            lastRequest = newRequest;
            connectionService.getOut().writeUTF(lastRequest.toString());
            controller.setUsername("");
            controller.setPassword("");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            controller.showAlert("Произошла ошибка при отправке данных на сервер");
        }
    }

    public void onSignUpClick(Event event) {
        controller.showSignUp(event);
    }

    public void onSignInClick(String username, String password) {
        this.username = username.trim();
        this.password = password.trim();
        if (!isValidCredentials(this.username, this.password)) {
            controller.showAlert("Указаны неполные данные авторизации");
            return;
        }
        initConnection();
    }

    private void initConnection() {
        connectionService = ConnectionService.getInstance();
        connectionService.setResponseListener(this);
        connectionService.addConnectionStateListener(connectionStateListener);
        if (connectionService.isConnected())
            signIn();
        else connectionService.connect(Constants.SERVER_IP, Constants.SERVER_PORT);
    }

    private boolean isValidCredentials(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    @Override
    public void onNewFile(File requestFile) {

    }

    @Override
    public void onNewMessage(String message) {
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
                            controller.updateUI(false);
                            break;
                        case 1:
                            controller.showCloudClient();
                            break;
                        case 2:
                            // TODO: hide progress
                            controller.updateUI(true);
                            Utils.showAlert("Неверый формат данных. Обратитесь к разработчику.");
                            break;
                        case 3:
                            // TODO: hide progress
                            controller.updateUI(true);
                            Utils.showAlert("Неверный логин и/или пароль");
                            break;
                        case 4:
                            // TODO: hide progress
                            controller.updateUI(true);
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

    public void onClose() {
        // TODO: intercept close controller
        connectionService.removeConnectionStateListener(connectionStateListener);
    }
}
