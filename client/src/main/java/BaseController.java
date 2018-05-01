public abstract class BaseController {

    public BaseController() {
    }

    public void showAlert(String message) {
        ClientUtils.showAlert(message);
    }
}
