package protocol;

import org.json.JSONException;

public class MessageParser {

    /**
     * Parse raw message
     * @param messageData raw message
     * @param lastRequest last command @see {@link RequestMessage}
     * @throws JSONException
     */
    public static void parse(byte[] messageData, RequestMessage lastRequest) throws JSONException {
        String strMessage = MessageUtil.toString(messageData);
        MessageType msgType = MessageUtil.getType(strMessage);
        switch (msgType) {
            case REQUEST:
                RequestMessage request = RequestMessage.parse(strMessage);
                MessageHandler.getInstance().handleRequest(request);
                break;
            case RESPONSE:
                ResponseMessage response = ResponseMessage.parse(strMessage);
                MessageHandler.getInstance().handleResponse(response, lastRequest.getCmd());
                break;
            case UNKNOWN:
                break;
            default:
                System.out.println((String.format("Unknown message type: type=%s, message=%s", msgType, strMessage)));
                break;
        }
    }
}