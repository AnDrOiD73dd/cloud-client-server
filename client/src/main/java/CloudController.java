import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class CloudController extends BaseController implements Initializable {

    private static final int COLUMN_MIN_WIDTH = 50;

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

    private ObservableList<ClientFile> clientFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presenter.initialize();
        configureTable();
    }

    private void configureTable() {
        fileTable.setEditable(true);

        TableColumn fileNameCol = new TableColumn("Имя");
        TableColumn filePathCol = new TableColumn("Путь");
        TableColumn fileSizeCol = new TableColumn("Размер");
        TableColumn fileDateCol = new TableColumn("Дата");
        TableColumn fileStatusCol = new TableColumn("Статус");

        fileNameCol.setCellValueFactory(
                new PropertyValueFactory<ClientFile, String>("fileName")
        );
        fileNameCol.setEditable(false);
        fileNameCol.setMinWidth(COLUMN_MIN_WIDTH);

        filePathCol.setCellValueFactory(
                new PropertyValueFactory<ClientFile, String>("filePath")
        );
        filePathCol.setEditable(false);
        filePathCol.setMinWidth(COLUMN_MIN_WIDTH);

        fileDateCol.setCellValueFactory(
                new PropertyValueFactory<ClientFile, Long>("fileDate")
        );
        fileDateCol.setCellFactory(column -> {
            TableCell<ClientFile, Date> cell = new TableCell<>() {
                private SimpleDateFormat format = new SimpleDateFormat(Utils.DATE_FORMAT);

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        this.setText(format.format(item));
                    }
                }
            };
            return cell;
        });
        fileDateCol.setEditable(false);
        fileDateCol.setMinWidth(COLUMN_MIN_WIDTH);

        fileSizeCol.setCellValueFactory(
                new PropertyValueFactory<ClientFile, Date>("fileSize")
        );
        fileSizeCol.setEditable(false);
        fileSizeCol.setMinWidth(COLUMN_MIN_WIDTH);

        fileStatusCol.setCellValueFactory(
                new PropertyValueFactory<ClientFile, String>("status")
        );
        fileStatusCol.setEditable(false);
        fileStatusCol.setMinWidth(COLUMN_MIN_WIDTH);
        fileTable.getColumns().addAll(fileNameCol, filePathCol, fileSizeCol, fileDateCol, fileStatusCol);
        fileTable.setItems(clientFiles);
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
            ClientUtils.setupIcon(stage, getClass());
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

    public void setClientFiles(ObservableList<ClientFile> clientFiles) {
        this.clientFiles = clientFiles;
    }

    public void updateTableData(ObservableList<ClientFile> clientFiles) {
        setClientFiles(clientFiles);
        fileTable.setItems(clientFiles);
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
