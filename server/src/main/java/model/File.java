package model;

public class File {

    public long id;
    public long user;
    public String filePath;
    public String fileSize;
    public String fileDate;
    public boolean synced;
    public String lastAction;

    public enum FileAction {
        ADD_BY_CLIENT,
        UPDATE_BY_CLIENT,
        DELETE_BY_CLIENT,
        SYNCED
    }
}
