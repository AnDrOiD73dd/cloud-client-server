package protocol.handler;

import protocol.request.RequestFilesList;

public interface FilesRequestHandler {
    void handleFilesListRequest(RequestFilesList requestFilesList);
}
