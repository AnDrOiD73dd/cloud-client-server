package ui.controller;

import base.ClientUtils;

public abstract class BaseController {

    public BaseController() {
    }

    public void showAlert(String message) {
        ClientUtils.showAlert(message);
    }
}
