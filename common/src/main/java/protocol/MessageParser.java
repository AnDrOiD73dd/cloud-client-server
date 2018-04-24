package protocol;

import org.json.JSONException;

public class MessageParser {

    /**
     * Parse raw message
     * @param messageData raw message
     * @param lastRequest last command @see {@link RequestMessage}
     * @throws JSONException if there is an error while parsing
     */
    public static void parse(byte[] messageData, RequestMessage lastRequest, RequestHandler requestHandler, ResponseHandler responseHandler) throws JSONException {
        String strMessage = MessageUtil.toString(messageData);
        parse(strMessage, lastRequest, requestHandler, responseHandler);
    }

    public static void parse(String strMessage, RequestMessage lastRequest, RequestHandler requestHandler, ResponseHandler responseHandler) throws JSONException {
        MessageType msgType = MessageUtil.getType(strMessage);
        switch (msgType) {
            case REQUEST:
                RequestMessage request = RequestMessage.parse(strMessage);
                requestHandler.handleRequest(request);
                break;
            case RESPONSE:
                ResponseMessage response = ResponseMessage.parse(strMessage);
                responseHandler.handleResponse(response, lastRequest.getCmd());
                break;
            case UNKNOWN:
                break;
            default:
                System.out.println((String.format("Unknown message type: type=%s, message=%s", msgType, strMessage)));
                break;
        }
    }
}