package com.administrator.projecte;

import java.util.Date;

/**
 * Created by bluekey630 on 5/25/2017.
 */

public class ChatMessage {


    private String messageText;
    private String messageUser;
    private String messageGroup;
    private long messageTime;
    private String imagUrl;

    public ChatMessage(String messageText, String messageUser, String messageGroup, String imagUrl) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageGroup = messageGroup;
        this.imagUrl = imagUrl;
        messageTime = new Date().getTime();
    }

    public String getImagUrl() {
        return imagUrl;

    }

    public void setImagUrl(String imagUrl) {
        this.imagUrl = imagUrl;
    }

    public ChatMessage() {

    }

    public String getMessageGroup() {
        return messageGroup;
    }

    public void setMessageGroup(String messageGroup) {
        this.messageGroup = messageGroup;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
