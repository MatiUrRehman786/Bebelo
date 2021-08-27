package com.buzzware.bebelo.eventBusModel;

public class MessageEvent {
    public MessageEvent(boolean isOpened) {
        this.isOpened = isOpened;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    boolean isOpened;
}
