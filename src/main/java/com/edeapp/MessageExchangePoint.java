package com.edeapp;

public class MessageExchangePoint {
    private static MessageExchangePoint instance;
    private Controller controller;
    private PopupController popupController;
    private String data;

    private MessageExchangePoint() {}

    public static MessageExchangePoint getInstance() {
        if (instance == null) {
            instance = new MessageExchangePoint();
        }
        return instance;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        if (this.controller == null)
            this.controller = controller;
    }

    public PopupController getPopupController() {
        return popupController;
    }

    public void setPopupController(PopupController popupController) {
        if (this.popupController == null)
            this.popupController = popupController;

    }
}
