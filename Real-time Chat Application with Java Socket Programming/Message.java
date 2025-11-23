package com.chat.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String username;
    private String content;
    private String room;
    private LocalDateTime timestamp;
    private MessageType type;
    
    public enum MessageType {
        CHAT, JOIN, LEAVE, SYSTEM, PRIVATE, COMMAND
    }
    
    public Message(String username, String content, String room, MessageType type) {
        this.username = username;
        this.content = content;
        this.room = room;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getRoom() { return room; }
    public MessageType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    @Override
    public String toString() {
        switch (type) {
            case CHAT:
                return String.format("[%s] %s: %s", getFormattedTimestamp(), username, content);
            case JOIN:
                return String.format("[%s] %s joined the room", getFormattedTimestamp(), username);
            case LEAVE:
                return String.format("[%s] %s left the room", getFormattedTimestamp(), username);
            case SYSTEM:
                return String.format("[%s] SYSTEM: %s", getFormattedTimestamp(), content);
            case PRIVATE:
                return String.format("[%s] PRIVATE from %s: %s", getFormattedTimestamp(), username, content);
            default:
                return content;
        }
    }
}