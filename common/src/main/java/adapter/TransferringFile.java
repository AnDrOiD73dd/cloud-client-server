package adapter;

import java.io.Serializable;

public class TransferringFile implements Serializable {

    private String filePath;
    private byte[] file;

    public TransferringFile(String filePath, byte[] file) {
        this.filePath = filePath;
        this.file = file;
    }

    public String getFilePath() {
        return filePath;
    }

    public byte[] getFile() {
        return file;
    }
}
