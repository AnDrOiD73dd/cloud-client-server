import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class CloudController implements Initializable {

    @FXML
    public TextField message;
    @FXML
    public TextArea messageHistory;
    @FXML
    public Button sendButton;
    @FXML
    public HBox authPanel;
    @FXML
    public HBox msgPanel;
    @FXML
    public TextField login;
    @FXML
    public PasswordField password;
    @FXML
    ListView<String> clientsListView;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Thread readFromServer;
    private boolean authorized;
    private String myNick;
    private ObservableList<String> clientList;
    private CloudPresenter presenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Platform.runLater(() -> login.requestFocus());
//        setAuthorized(false);
    }

    public CloudController() {
        presenter = new CloudPresenter(this);
    }

    private void connect() {
        try {
            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            clientList = FXCollections.observableArrayList();
            readFromServer = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        String s = in.readUTF();
                        parseMsg(s);
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                    showAlert("Сервер перестал отвечать");
                    setAuthorized(false);
                    disconnect();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Не удалось закрыть сокет: ");
                        e.printStackTrace();
                    }
                }
            });
            readFromServer.setDaemon(true);
            readFromServer.start();
        } catch (IOException e) {
//            e.printStackTrace();
            showAlert("Не удалось подключиться к серверу. Проверьте сетевое соединение.");
        }
    }

    private void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readFromServer.interrupt();
    }

    private void parseMsg(String s) {
        if (s.startsWith(Constants.PREFIX))
            parseServerCommand(s);
        else messageHistory.appendText(s + "\n");
    }

    private void parseServerCommand(String s) {
//        if (s.startsWith(Constants.INFO_MESSAGE)) {
//            showAlert(s.substring(Constants.INFO_MESSAGE.length() + 1));
//            return;
//        }
        if (s.startsWith(Constants.AUTH_RESPONSE_OK)) {
            setAuthorized(true);
            login.clear();
            password.clear();
            System.out.println(s);
            myNick = s.split("\\s")[1];
            return;
        }
        if (s.startsWith(Constants.CLIENTS_LIST + " ")) {
            String[] data = s.split("\\s");
            Platform.runLater(() -> {
                clientList.clear();
                for (int i = 1; i < data.length; i++) {
                    clientList.addAll(data[i]);
                }
                configureClientListView();
            });
            return;
        }
        switch (s) {
            case Constants.AUTH_RESPONSE_FAIL:
                showAlert("Ошибка при аутентификаии. Возможно неверный логин или пароль.");
                break;
            default:
                System.out.println("Нераспознанная команда: " + s);
                break;
        }
    }

    private void configureClientListView() {
        clientsListView.setItems(clientList);
        clientsListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty){
                            setText(item);
                            if (item.equals(myNick)) {
                                setStyle("-fx-font-weight: bold; -fx-background-color: #607D8B;");
                            }
                        }else{
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    public void sendMsg() {
        String msg = message.getText();

        if (msg.isEmpty())
            return;

        try {
            out.writeUTF(msg);
            message.clear();
            message.requestFocus();
        } catch (IOException e) {
//            e.printStackTrace();
            showAlert("Произошла ошибка при отправке сообщения. Возможно потеряно соединение с сервером.");
        }

        if (msg.equals(Constants.LOGOUT_CMD)) {
            disconnect();
            setAuthorized(false);
            closeWindow();
        }
    }

    public void sendAuthMsg() {
        if (login.getText().isEmpty()) {
            showAlert("Поле \"логин\" не может быть пустым");
            return;
        }
        if (password.getText().isEmpty()) {
            showAlert("Поле \"пароль\" не может быть пустым");
            return;
        }

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {// /auth loginField pass
            out.writeUTF(String.format("%s %s %s", Constants.AUTH_REQUEST, login.getText(), password.getText()));
//            loginField.clear();
//            passwordField.clear();
        } catch (IOException e) {
//            e.printStackTrace();
            showAlert("Произошла ошибка при аутентификации. Возможно потеряно соединение с сервером.");
        }
    }

    private void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        updateUI();
    }

    private void updateUI() {
        msgPanel.setVisible(authorized);
        msgPanel.setManaged(authorized);
        authPanel.setVisible(!authorized);
        authPanel.setManaged(!authorized);
        clientsListView.setVisible(authorized);
        clientsListView.setManaged(authorized);
        myNick = authorized ? myNick : "";
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Возникли проблемы");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    private void closeWindow() {
        // get a handle to the stage
        Stage stage = (Stage) messageHistory.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    public void clientsListClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            String addressee = clientsListView.getSelectionModel().getSelectedItem();
//            if (!addressee.equals(myNick)) {
//                message.setText(Constants.PRIVATE_MSG + " " + addressee + " ");
//                message.requestFocus();
//                message.selectEnd();
//            }
        }
    }

    public void onClickAdd(ActionEvent actionEvent) {
    }

    public void onClickDelete(ActionEvent actionEvent) {
    }
}
