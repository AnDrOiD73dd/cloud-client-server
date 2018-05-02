package protocol.handler;

import protocol.CommandList;
import protocol.ResponseMessage;

public interface ResponseHandler {

    /**
     * Parse response message
     * @param responseMessage @see {@link ResponseMessage}
     * @param command @see {@link CommandList}
     */
    void handleResponse(ResponseMessage responseMessage, String command);
}
