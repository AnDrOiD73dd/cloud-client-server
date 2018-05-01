import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ClientFile {

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
        String status = "Синхронизировано";
        String filePath = file.getFilePath();
        String fileName = FileService.getName(filePath);
        if (FileService.isExists(filePath)) {
            try {
                long cloudSize = file.getFileSize();
                long localSize = FileService.getSize(filePath);
                if (cloudSize == localSize) {
                    if (file.getFileDate() != FileService.getDate(filePath))
                        status = "Времена изменения файлов раличаются";
                }
                else {
                    if (cloudSize > localSize)
                        status = "Размер файла в облаке больше";
                    else status = "Размер локального файла больше";
                }
            } catch (IOException e) {
                status = "Ошибка при проверке размера файла";
            }
        } else status = "Файл отсутствует";
//        Date fileDate = Utils.getDate(file.getFileDate());
        Date fileDate = Utils.getDate(System.currentTimeMillis());
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
