package com.cync.model;

public class ChatMessageNotification {

    public String count;
    public String profileImage,name;
    public ChatMessage chatMessages;

    public ChatMessageNotification(String count, String profileImage,String name, ChatMessage chatMessages) {
        super();
        this.count = count;
        this.profileImage=profileImage;
        this.name=name;
        this.chatMessages = chatMessages;
    }

}
