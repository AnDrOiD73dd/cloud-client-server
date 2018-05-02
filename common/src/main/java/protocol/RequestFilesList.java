package protocol;

import model.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class RequestFilesList extends RequestMessage {

    public static final String KEY_REQUEST_BODY = "request";
    private static final String KEY_COMMAND = "cmd";
    private static final String KEY_REQUEST_DATA = "data";
    public static final String KEY_FILE_PATH = "filePath";
    public static final String KEY_DESTINATION_FILE_PATH = "destination filePath";
    public static final String KEY_FILE_DATE = "fileDate";
    public static final String KEY_FILE_SIZE = "fileSize";
    private static final String KEY_SYNCED = "synced";

    private int id;
    private String cmd;
    private ArrayList<File> filesList;

    public RequestFilesList(int id) {
        super(id, CommandList.FILES_LIST);
        this.id = id;
        this.cmd = CommandList.FILES_LIST;
        this.filesList = new ArrayList<>();
    }

    public RequestFilesList(int id, ArrayList<File> filesList) {
        super(id, CommandList.FILES_LIST);
        this.id = id;
        this.cmd = CommandList.FILES_LIST;
        this.filesList = filesList;
    }

    public static RequestFilesList parse(String jsonMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonMessage);
        int id = jsonObject.getInt(Message.KEY_ID);
        JSONObject requestBody = jsonObject.getJSONObject(KEY_REQUEST_BODY);
//        String cmd = requestBody.getString(KEY_COMMAND);
        ArrayList<File> filesList = new ArrayList<>();
        if (requestBody.has(KEY_REQUEST_DATA)) {
            JSONArray dataBody = requestBody.getJSONArray(KEY_REQUEST_DATA);
            for (int i = 0; i < dataBody.length(); i++) {
                JSONObject jsonFile = dataBody.getJSONObject(i);
                File file = new File.Builder()
                        .setFilePath(jsonFile.getString(KEY_FILE_PATH))
                        .setFileDate(jsonFile.getLong(KEY_FILE_DATE))
                        .setFileSize(jsonFile.getLong(KEY_FILE_SIZE))
                        .setSynced(jsonFile.getBoolean(KEY_SYNCED))
                        .create();
                filesList.add(file);
            }
        }
        return new RequestFilesList(id, filesList);
    }

    @Override
    public int getId() {
        return id;
    }

    public String getCmd() {
        return cmd;
    }

    public ArrayList<File> getFilesList() {
        return filesList;
    }

    /**
     * Convert JSON representation of request to byte array
     * @return byte array of JSON representation
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    @Override
    public byte[] toByteArray() throws UnsupportedEncodingException, JSONException {
        return getJson().toString().getBytes(DEFAULT_ENCODING);
    }

    /**
     * Combine top level of request
     * @return JSON representation of instance
     * @throws JSONException
     */
    private JSONObject getJson() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(KEY_ID, getId());
        result.put(KEY_REQUEST_BODY, generateRequest());
        return result;
    }

    /**
     * Combine middle level of request
     * @return JSON representation of middle level
     * @throws JSONException
     */
    private JSONObject generateRequest() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(KEY_COMMAND, cmd);
        if (filesList.size() > 0)
            result.put(KEY_REQUEST_DATA, generateRequestData());
        return result;
    }

    /**
     * Combine low level of request
     * @return JSON representation of low level
     * @throws JSONException
     */
    private JSONArray generateRequestData() throws JSONException {
        JSONArray result = new JSONArray();
        for (File file : filesList)
        {
            JSONObject jsonFile = new JSONObject();
            jsonFile.put(KEY_FILE_PATH, file.getFilePath());
            jsonFile.put(KEY_FILE_DATE, file.getFileDate());
            jsonFile.put(KEY_FILE_SIZE, file.getFileSize());
            jsonFile.put(KEY_SYNCED, file.isSynced());
            result.put(jsonFile);
        }
        return result;
    }

    @Override
    public String toString() {
        try {
            return getJson().toString();
        } catch (JSONException e) {
            return "";
        }
    }
}
