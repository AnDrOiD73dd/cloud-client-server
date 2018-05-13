package ui.controller;

import base.ClientUtils;
import base.Constants;
import base.Utils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.ClientFile;
import ui.presenter.CloudPresenter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class CloudController extends BaseController implements Initializable {

    private static final int COLUMN_MIN_WIDTH = 50;

    @FXML
    public TableView fileTable;
    @FXML
    public Button btAdd;
    @FXML
    public Button btUpdate;
    @FXML
    public Button btDelete;
    @FXML
    public Button btDeleteAll;
    @FXML
    public Button btDownload;

    private CloudPresenter presenter;

    private ObservableList<ClientFile> clientFiles;
    private final FileChooser fileChooser = new FileChooser();

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
                new PropertyValueFactory<ClientFile, String>("fileSize")
        );
        fileSizeCol.setEditable(false);
        fileSizeCol.setMinWidth(COLUMN_MIN_WIDTH);

        fileStatusCol.setCellValueFactory(
                new PropertyValueFactory<ClientFile, String>("status")
        );
        fileStatusCol.setCellFactory((Callback<TableColumn<ClientFile, String>, TableCell<ClientFile, String>>) param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item.equals(ClientFile.STATUS_SYNCED)) {
//                        setTextFill(Color.BLACK);
//                        setStyle("-fx-font-weight: bold");
                        setStyle("-fx-background-color: #FFFFFF");
                        setText(item);
                    } else if (item.equals(ClientFile.STATUS_FILE_NOT_FOUND)) {
//                        setTextFill(Color.RED);
//                        setStyle("-fx-font-weight: bold");
                        setStyle("-fx-background-color: #F44336");
                        setText(item);
                    } else if (item.equals(ClientFile.STATUS_SIZE_IS_MORE) || item.equals(ClientFile.STATUS_SIZE_IS_SMALLER)) {
//                        setTextFill(Color.BLACK);
//                        setStyle("-fx-font-weight: bold");
                        setStyle("-fx-background-color: #FFEB3B");
                        setText(item);
                    } else {
//                        setTextFill(Color.WHITE);
//                        setStyle("-fx-font-weight: bold");
                        setStyle("-fx-background-color: #BDBDBD");
                        setText(item);
                    }
                }
            }
        });
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
        presenter.onClickDelete(actionEvent, getSelectedItem());
    }

    public void onClickDeleteAll(ActionEvent actionEvent) {
        presenter.onClickDeleteAll(actionEvent, getSelectedItem());
    }

    public void onClickDownload(ActionEvent actionEvent) {
        presenter.onClickDownload(actionEvent, getSelectedItem());
    }

    public void onClickUpdate(ActionEvent actionEvent) {
        presenter.onClickUpdate(actionEvent, getSelectedItem());
    }

    public void showSignIn() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ui/layout/layout_sign_in.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Constants.TITLE_SIGN_IN);
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

    private void setClientFiles(ObservableList<ClientFile> clientFiles) {
        this.clientFiles = clientFiles;
    }

    public void updateTableData(ObservableList<ClientFile> clientFiles) {
        setClientFiles(clientFiles);
        fileTable.setItems(clientFiles);
//        if (this.clientFiles == null) {
//            setClientFiles(clientFiles);
//            fileTable.setItems(clientFiles);
//        } else {
//            for (model.ClientFile clientFile : clientFiles) {
//                if (!this.clientFiles.contains(clientFile))
//                    this.clientFiles.add(clientFile);
//            }
//        }
    }

    ClientFile getSelectedItem() {
        return (ClientFile) fileTable.getSelectionModel().getSelectedItem();
    }

    public File showFileOpenDialog() {
        return fileChooser.showOpenDialog(fileTable.getScene().getWindow());
    }

    File showFileSaveDialog() {
        return fileChooser.showSaveDialog(fileTable.getScene().getWindow());
    }

    public void showReplaceDialog(String filePath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Выберите действие");
        alert.setHeaderText("Файл уже существует, что будем делать?");
        alert.setContentText("Выбранный файл уже существует. Желаете заменить его или указать другое место для загрузки файла?");

        ButtonType buttonReplace = new ButtonType("Заменить");
        ButtonType buttonChoosePath = new ButtonType("Выбрать другой путь");
        ButtonType buttonCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonReplace, buttonChoosePath, buttonCancel);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/images/happy-cloud-480.png").toString()));

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonReplace){
            presenter.onDownloadDialogResult(filePath, filePath);
        }
        else if (result.get() == buttonChoosePath) {
            File file = showFileSaveDialog();
            if (file != null) {
                presenter.onDownloadDialogResult(filePath, file.getAbsolutePath());
            }
        }
    }
}
