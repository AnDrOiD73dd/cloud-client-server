public class Constants {
    public static final String PREFIX = "/";

    public static final String AUTH_REQUEST = PREFIX + "login";
    public static final String AUTH_RESPONSE_OK = PREFIX + "login_ok";
    public static final String AUTH_RESPONSE_FAIL = PREFIX + "login_fail";

    public static final String LOGOUT_CMD = PREFIX + "logout";

    public static final String FILES_LIST = PREFIX + "files_list";
    public static final String CLIENTS_LIST = PREFIX + "clients_list";

    public static final String ADD_FILE = PREFIX + "add_file";
    public static final String DELETE_FILE = PREFIX + "delete_file";

    public static final String TERMINATE_SERVER = PREFIX + "closeDb";

    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 8189;
}
