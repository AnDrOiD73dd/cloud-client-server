package protocol;

public class MessageHandler {

    private static MessageHandler instance;

    private MessageHandler() {
    }

    public static MessageHandler getInstance() {
        MessageHandler localInstance = instance;
        if (localInstance == null) {
            synchronized (MessageHandler.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MessageHandler();
                }
            }
        }
        return localInstance;
    }

    /**
     * Parse response message
     * @param responseMessage @see {@link ResponseMessage}
     * @param command @see {@link CommandList}
     */
    public void handleResponse(ResponseMessage responseMessage, String command) {
        System.out.println("handleResponse() responseCode=" + responseMessage.getResponseCode() + ", cmd=" + command);
        switch (command) {
            case CommandList.SIGN_IN:
                switch (responseMessage.getResponseCode()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                                + ", cmd=" + CommandList.SIGN_IN);
                        break;
                }
                break;
            case CommandList.SIGN_OUT:
                switch (responseMessage.getResponseCode()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                                + ", cmd=" + CommandList.SIGN_OUT);
                        break;
                }
                break;
            case CommandList.SIGN_UP:
                switch (responseMessage.getResponseCode()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                                + ", cmd=" + CommandList.SIGN_UP);
                        break;
                }
                break;
            default:
                System.out.println(("Unknown command=" + command));
                break;
        }
    }

    /**
     * Parse request message
     * @param requestMessage @see {@link RequestMessage}
     */
    public void handleRequest(RequestMessage requestMessage) {
        System.out.println("handleRequest: RequestMessage=" + requestMessage.toString());
        int id = requestMessage.getId();
//        switch (id) {
//            case Message.HOST_MSG_ID:
//                switch (requestMessage.getCmd()) {
//                    case CommandList.SIGN_OUT:
//                        break;
//                    default:
//                        System.out.println(String.format("parseRequest::Unknown request CMD: %s", requestMessage.toString()));
//                        break;
//                }
//                break;
//            default:
//                System.out.println(String.format("parseRequest::Unknown request ID: %s", requestMessage.toString()));
//                break;
//        }
    }
}
