package protocol;

import model.File;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestMessageFactory {

    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "first name";
    public static final String KEY_LAST_NAME = "last name";
    public static final String KEY_EMAIL = "email";

    /**
     * Generate @see {@link Message} instance from input params to login operation
     * @param id request id
     * @param login user login/token/id
     * @param password user password
     * @return @see {@link Message} instance
     */
    public static Message getLoginMessage(int id, String login, String password) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(KEY_LOGIN, login);
        dataMap.put(KEY_PASSWORD, password);
        return new RequestMessage(id, CommandList.SIGN_IN, dataMap);
    }

    /**
     * Generate @see {@link Message} instance from input params to logout operation
     * @param id request id
     * @return @see {@link Message} instance
     */
    public static Message getLogoutMessage(int id) {
        return new RequestMessage(id, CommandList.SIGN_OUT);
    }

    /**
     * Generate @see {@link Message} instance from input params to register operation
     * @param id request id
     * @return @see {@link Message} instance
     */
    public static Message getSignUpMessage(int id, String username, String password, String firtName, String lastName, String email) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(KEY_LOGIN, username);
        dataMap.put(KEY_PASSWORD, password);
        dataMap.put(KEY_FIRST_NAME, firtName);
        dataMap.put(KEY_LAST_NAME, lastName);
        dataMap.put(KEY_EMAIL, email);
        return new RequestMessage(id, CommandList.SIGN_UP, dataMap);
    }

    public static Message getEmptyFilesListRequest(int id) {
        return new RequestFilesList(id);
    }

    public static Message getFilesListRequest(int id, ArrayList<File> filesList) {
        return new RequestFilesList(id, filesList);
    }

    public static Message getFileAddRequest(int id, String filepath, long fileDate, long fileSize) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(RequestFilesList.KEY_FILE_PATH, filepath);
        dataMap.put(RequestFilesList.KEY_FILE_DATE, String.valueOf(fileDate));
        dataMap.put(RequestFilesList.KEY_FILE_SIZE, String.valueOf(fileSize));
        return new RequestMessage(id, CommandList.FILE_ADD, dataMap);
    }

    public static Message getFileDeleteRequest(int id, String filepath) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(RequestFilesList.KEY_FILE_PATH, filepath);
        return new RequestMessage(id, CommandList.FILE_DELETE, dataMap);
    }
}

