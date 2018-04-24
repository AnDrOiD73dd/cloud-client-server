package protocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class MessageUtil {

    private static Random random = new Random();

    /**
     * Generate random id
     * @return generated id
     */
    public static int getId() {
        return random.nextInt(1000) + 1;
    }

    /**
     * Determines the type of message
     * @param jsonMessage JSON representation of message
     * @return @see {@link MessageType}
     */
    static MessageType getType(String jsonMessage) {
        MessageType res;
        try {
            JSONObject jsonObject = new JSONObject(jsonMessage);
            if (jsonObject.has(RequestMessage.KEY_REQUEST_BODY)
                    && !jsonObject.has(ResponseMessage.KEY_RESPONSE)) {
                res = MessageType.REQUEST;
            }
            else if (!jsonObject.has(RequestMessage.KEY_REQUEST_BODY)
                    && jsonObject.has(ResponseMessage.KEY_RESPONSE)) {
                res = MessageType.RESPONSE;
            }
            else res = MessageType.UNKNOWN;
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            res = MessageType.UNKNOWN;
        }
        return res;
    }

    /**
     * Convert byte array representation of message to String
     * @param messageData message in byte array
     * @return String representation of message or null if the UnsupportedEncodingException was catched
     */
    static String toString(byte[] messageData) {
        try {
            return new String(messageData, Message.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

