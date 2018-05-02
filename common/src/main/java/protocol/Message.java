package protocol;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public interface Message {

    String KEY_ID = "id";
    String DEFAULT_ENCODING = "UTF-8";

    /**
     * Return id
     * @return value of field id
     */
    int getId();

    /**
     * Convert object to byte array representation
     * @return Return byte array representation of instance (usually from JSON representation)
     * @throws JSONException if there is an error while parsing
     */
    byte[] toByteArray() throws UnsupportedEncodingException, JSONException;
}
