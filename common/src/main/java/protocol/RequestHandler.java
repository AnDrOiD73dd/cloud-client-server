package protocol;

public interface RequestHandler {

    /**
     * Parse request message
     * @param requestMessage @see {@link RequestMessage}
     */
    void handleRequest(RequestMessage requestMessage);
}
