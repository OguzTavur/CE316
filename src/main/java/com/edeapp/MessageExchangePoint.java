package com.edeapp;


// This class handles communication between two controller classes
// The design pattern of this class is Singleton
// When the object of this class called it won't create a new MessageExchangePoint class
// Also other controller classes have same design structure


public class MessageExchangePoint {
    private static MessageExchangePoint instance;
    private Controller controller;
    private PopupController popupController;

    private MessageExchangePoint() {}

    public static MessageExchangePoint getInstance() {
        if (instance == null) {
            instance = new MessageExchangePoint();
        }
        return instance;
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
        if (this.popupController == null || popupController == null)
            this.popupController = popupController;


    }
}
