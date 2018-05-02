package adapter;

public class File {

    private long id;
    private long userId;
    private String serverFileName;
    private String filePath;
    private long fileSize;
    private long fileDate;
    private boolean synced;
    private String lastAction;

    public enum FileAction {
        ADD_BY_CLIENT,
        UPDATE_BY_CLIENT,
        DELETE_BY_CLIENT,
        SYNCED
    }

    private File(long id, long userId, String serverFileName, String filePath, long fileSize, long fileDate, boolean synced, String lastAction) {
        this.id = id;
        this.userId = userId;
        this.serverFileName = serverFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileDate = fileDate;
        this.synced = synced;
        this.lastAction = lastAction;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileDate(long fileDate) {
        this.fileDate = fileDate;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getServerFileName() {
        return serverFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getFileDate() {
        return fileDate;
    }

    public boolean isSynced() {
        return synced;
    }

    public String getLastAction() {
        return lastAction;
    }

    public static class Builder {
        private long id;
        private long userId;
        private String serverFileName;
        private String filePath;
        private long fileSize;
        private long fileDate;
        private boolean synced;
        private String lastAction;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setServerFileName(String serverFileName) {
            this.serverFileName = serverFileName;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setFileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder setFileDate(long fileDate) {
            this.fileDate = fileDate;
            return this;
        }

        public Builder setSynced(boolean synced) {
            this.synced = synced;
            return this;
        }

        public Builder setLastAction(String lastAction) {
            this.lastAction = lastAction;
            return this;
        }

        public File create() {
            return new File(id, userId, serverFileName, filePath, fileSize, fileDate, synced, lastAction);
        }
    }
}
