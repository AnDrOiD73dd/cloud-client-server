package protocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class RequestMessage implements Message {

    public static final String KEY_REQUEST_BODY = "request";
    private static final String KEY_COMMAND = "cmd";
    private static final String KEY_REQUEST_DATA = "data";

    private int id;
    private String cmd;
    private HashMap<String, String> request;

    public RequestMessage(int id, String cmd, HashMap<String, String> request) {
        this.id = id;
        this.cmd = cmd;
        this.request = request;
    }

    public RequestMessage(int id, String cmd) {
        this.id = id;
        this.cmd = cmd;
        this.request = new HashMap<>();
    }

    /**
     * Parse and convert string JSON to @{@link RequestMessage} instance
     * @param jsonMessage JSON string, which correspond to protocol
     * @throws JSONException
     */
    public static RequestMessage parse(String jsonMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonMessage);
        int id = jsonObject.getInt(Message.KEY_ID);
        JSONObject requestBody = jsonObject.getJSONObject(KEY_REQUEST_BODY);
        String cmd = requestBody.getString(KEY_COMMAND);
        JSONObject dataBody = requestBody.getJSONObject(KEY_REQUEST_DATA);
        HashMap<String, String> request = new HashMap<>();
        for (Iterator<String> it = dataBody.keys(); it.hasNext(); ) {
            String key = it.next();
            request.put(key, dataBody.getString(key));
        }
        return new RequestMessage(id, cmd, request);
    }

    /**
     * Return request id
     * @return request id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Get command
     * @return command
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * Get message request only
     * @return request representation
     */
    public HashMap<String, String> getRequest() {
        return request;
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
        if (!request.isEmpty())
            result.put(KEY_REQUEST_DATA, generateRequestData());
        return result;
    }

    /**
     * Combine low level of request
     * @return JSON representation of low level
     * @throws JSONException
     */
    private JSONObject generateRequestData() throws JSONException {
        JSONObject result = new JSONObject();
        for (HashMap.Entry<String, String> entry : request.entrySet())
        {
            result.put(entry.getKey(), entry.getValue());
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
