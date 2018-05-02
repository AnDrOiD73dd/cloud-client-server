import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ClientFile {

    public static final String STATUS_SYNCED = "Синхронизировано";
    public static final String STATUS_FILE_NOT_FOUND = "Файл отсутствует";
    public static final String STATUS_CHECK_SIZE_ERROR = "Ошибка при проверке размера файла";
    public static final String STATUS_TIME_IS_DIFFERENT = "Времена изменения файлов раличаются";
    public static final String STATUS_SIZE_IS_MORE = "Размер файла в облаке больше";
    public static final String STATUS_SIZE_IS_SMALLER = "Размер локального файла больше";

    private String fileName;
    private String filePath;
    private Date fileDate;
    private String fileSize;
    private String status;

    public ClientFile(String fileName, String filePath, Date fileDate, String fileSize, String status) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileDate = fileDate;
        this.fileSize = fileSize;
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public Date getFileDate() {
        return fileDate;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getStatus() {
        return status;
    }

    public static ObservableList<ClientFile> map(ArrayList<model.File> arrayList) {
        ArrayList<ClientFile> clientFiles = new ArrayList<>();
        for (model.File file : arrayList) {
            clientFiles.add(map(file));
        }
        return FXCollections.observableArrayList(clientFiles);
    }

    private static ClientFile map(model.File file) {
        String status = STATUS_SYNCED;
        String filePath = file.getFilePath();
        String fileName = FileService.getName(filePath);
        if (FileService.isExists(filePath)) {
            try {
                long cloudSize = file.getFileSize();
                long localSize = FileService.getSize(filePath);
                if (cloudSize == localSize) {
                    if (file.getFileDate() != FileService.getDate(filePath))
                        status = STATUS_TIME_IS_DIFFERENT;
                }
                else {
                    if (cloudSize > localSize)
                        status = STATUS_SIZE_IS_MORE;
                    else status = STATUS_SIZE_IS_SMALLER;
                }
            } catch (IOException e) {
                status = STATUS_CHECK_SIZE_ERROR;
            }
        } else status = STATUS_FILE_NOT_FOUND;
        Date fileDate = Utils.getDate(file.getFileDate());
        String fileSize = FileService.getHumanSize(file.getFileSize());
        return new ClientFile(fileName, filePath, fileDate, fileSize, status);
    }

    @Override
    public String toString() {
        return "ClientFile{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileDate=" + fileDate +
                ", fileSize=" + fileSize +
                ", status='" + status + '\'' +
                '}';
    }
}
