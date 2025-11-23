package com.chat.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

public class ChatRoom {
    private String name;
    private Map<String, Object> clients;
    private List<Message> messageHistory;
    private int maxHistorySize;
    
    public ChatRoom(String name) {
        this.name = name;
        this.clients = new ConcurrentHashMap<>();
        this.messageHistory = new CopyOnWriteArrayList<>();
        this.maxHistorySize = 100;
    }
    
    public String getName() { return name; }
    
    public void addClient(String username, Object clientHandler) {
        clients.put(username, clientHandler);
    }
    
    public void removeClient(String username) {
        clients.remove(username);
    }
    
    public boolean hasClient(String username) {
        return clients.containsKey(username);
    }
    
    public Map<String, Object> getClients() {
        return clients;
    }
    
    public int getClientCount() {
        return clients.size();
    }
    
    public void addMessage(Message message) {
        messageHistory.add(message);
        if (messageHistory.size() > maxHistorySize) {
            messageHistory.remove(0);
        }
    }
    
    public List<Message> getMessageHistory() {
        return new CopyOnWriteArrayList<>(messageHistory);
    }
    
    public boolean isEmpty() {
        return clients.isEmpty();
    }
}