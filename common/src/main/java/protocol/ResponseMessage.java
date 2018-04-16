package protocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ResponseMessage implements Message {

    public static final String KEY_RESPONSE = "responseCode";

    private int id;
    private int responseCode;

    public ResponseMessage(int id, int responseCode) {
        this.id = id;
        this.responseCode = responseCode;
    }

    /**
     * Get message id
     * @return message id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Get response code
     * @return response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Doing nothing
     * @return empty byte array
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    @Override
    public byte[] toByteArray() throws UnsupportedEncodingException, JSONException {
        return new byte[0];
    }

    /**
     * Parse and convert JSON string representation to @see {@link ResponseMessage}
     * @param data JSON string, which correspond to protocol
     * @throws JSONException
     */
    public static ResponseMessage parse(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        int id = jsonObject.getInt(Message.KEY_ID);
        int responseCode = jsonObject.getInt(KEY_RESPONSE);
        return new ResponseMessage(id, responseCode);
    }
}
