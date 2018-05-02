package protocol.handler;

import protocol.request.RequestMessage;

public interface RequestHandler {

    /**
     * Parse request message
     * @param requestMessage @see {@link RequestMessage}
     */
    void handleRequest(RequestMessage requestMessage);
}
