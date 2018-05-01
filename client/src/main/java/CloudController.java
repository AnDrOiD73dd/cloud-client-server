import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CloudController extends BaseController implements Initializable {

    @FXML
    public TableView fileTable;
    @FXML
    public Button btAdd;
    @FXML
    public Button btDelete;
    @FXML
    public Button btDeleteAll;
    @FXML
    public Button btDownload;
    @FXML
    public Button btUpdate;

    private CloudPresenter presenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presenter.initialize();
    }

    public CloudController() {
        presenter = new CloudPresenter(this);
    }

    public void onClickAdd(ActionEvent actionEvent) {
        presenter.onClickAdd(actionEvent);
    }

    public void onClickDelete(ActionEvent actionEvent) {
        presenter.onClickDelete(actionEvent);
    }

    public void onClickDeleteAll(ActionEvent actionEvent) {
        presenter.onClickDeleteAll(actionEvent);
    }

    public void onClickDownload(ActionEvent actionEvent) {
        presenter.onClickDownload(actionEvent);
    }

    public void onClickUpdate(ActionEvent actionEvent) {
        presenter.onClickUpdate(actionEvent);
    }

    void showSignIn() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("layout_sign_in.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Constants.APP_NAME + ": Вход в систему");
            Utils.setupIcon(stage, getClass());
            stage.setScene(new Scene(root));
            stage.show();
            // Hide this current window (if this is what you want)
            Stage currentStage = (Stage) fileTable.getScene().getWindow();
            currentStage.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

//    private void configureClientListView() {
//        clientsListView.setItems(clientList);
//        clientsListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//            @Override
//            public ListCell<String> call(ListView<String> param) {
//                return new ListCell<String>(){
//                    @Override
//                    protected void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (!empty){
//                            setText(item);
//                            if (item.equals(myNick)) {
//                                setStyle("-fx-font-weight: bold; -fx-background-color: #607D8B;");
//                            }
//                        }else{
//                            setGraphic(null);
//                            setText(null);
//                        }
//                    }
//                };
//            }
//        });
//    }
}
