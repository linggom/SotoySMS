package com.sotoy.spam.model;

public class SMS {

    private String sender;
    private String message;
    private boolean isSpam;

    public SMS(String sender, String message) {
        this(sender, message, false);
    }

    public SMS(String sender, String message, boolean isSpam) {
        setMessage(message);
        setSender(sender);
        setSpam(isSpam);
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }
}
