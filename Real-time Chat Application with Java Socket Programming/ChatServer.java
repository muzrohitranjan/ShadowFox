package com.chat.server;

import com.chat.common.ChatRoom;
import com.chat.common.Message;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 8080;
    private static final String DEFAULT_ROOM = "general";
    
    private ServerSocket serverSocket;
    private Map<String, ChatRoom> chatRooms;
    private Map<String, ClientHandler> connectedClients;
    private ExecutorService threadPool;
    private volatile boolean running;
    
    public ChatServer() {
        chatRooms = new ConcurrentHashMap<>();
        connectedClients = new ConcurrentHashMap<>();
        threadPool = Executors.newCachedThreadPool();
        
        // Create default room
        chatRooms.put(DEFAULT_ROOM, new ChatRoom(DEFAULT_ROOM));
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Chat Server started on port " + PORT);
            System.out.println("Waiting for clients to connect...");
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    threadPool.execute(clientHandler);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
    
    public synchronized void addClient(String username, ClientHandler clientHandler) {
        connectedClients.put(username, clientHandler);
        joinRoom(username, DEFAULT_ROOM);
        broadcastSystemMessage(DEFAULT_ROOM, username + " connected to the server");
        System.out.println("Client connected: " + username + " (Total: " + connectedClients.size() + ")");
    }
    
    public synchronized void removeClient(String username) {
        ClientHandler clientHandler = connectedClients.remove(username);
        if (clientHandler != null) {
            String currentRoom = clientHandler.getCurrentRoom();
            if (currentRoom != null) {
                leaveRoom(username, currentRoom);
            }
            broadcastSystemMessage(currentRoom != null ? currentRoom : DEFAULT_ROOM, 
                                 username + " disconnected from the server");
            System.out.println("Client disconnected: " + username + " (Total: " + connectedClients.size() + ")");
        }
    }
    
    public void joinRoom(String username, String roomName) {
        ChatRoom room = chatRooms.computeIfAbsent(roomName, ChatRoom::new);
        ClientHandler clientHandler = connectedClients.get(username);
        
        if (clientHandler != null) {
            // Leave current room if any
            String currentRoom = clientHandler.getCurrentRoom();
            if (currentRoom != null && !currentRoom.equals(roomName)) {
                leaveRoom(username, currentRoom);
            }
            
            room.addClient(username, clientHandler);
            clientHandler.setCurrentRoom(roomName);
            
            Message joinMessage = new Message(username, "", roomName, Message.MessageType.JOIN);
            room.addMessage(joinMessage);
            broadcastToRoom(roomName, joinMessage.toString(), username);
            
            // Send room history to the joining client
            clientHandler.sendMessage("=== Joined room: " + roomName + " ===");
            for (Message msg : room.getMessageHistory()) {
                if (msg.getType() == Message.MessageType.CHAT) {
                    clientHandler.sendMessage(msg.toString());
                }
            }
        }
    }
    
    public void leaveRoom(String username, String roomName) {
        ChatRoom room = chatRooms.get(roomName);
        if (room != null) {
            room.removeClient(username);
            
            Message leaveMessage = new Message(username, "", roomName, Message.MessageType.LEAVE);
            room.addMessage(leaveMessage);
            broadcastToRoom(roomName, leaveMessage.toString(), username);
            
            // Remove empty rooms (except default)
            if (room.isEmpty() && !DEFAULT_ROOM.equals(roomName)) {
                chatRooms.remove(roomName);
            }
        }
    }
    
    public void broadcastMessage(String roomName, String username, String content) {
        ChatRoom room = chatRooms.get(roomName);
        if (room != null) {
            Message message = new Message(username, content, roomName, Message.MessageType.CHAT);
            room.addMessage(message);
            broadcastToRoom(roomName, message.toString(), null);
        }
    }
    
    public void broadcastSystemMessage(String roomName, String content) {
        ChatRoom room = chatRooms.get(roomName);
        if (room != null) {
            Message message = new Message("SYSTEM", content, roomName, Message.MessageType.SYSTEM);
            room.addMessage(message);
            broadcastToRoom(roomName, message.toString(), null);
        }
    }
    
    private void broadcastToRoom(String roomName, String message, String excludeUser) {
        ChatRoom room = chatRooms.get(roomName);
        if (room != null) {
            for (Map.Entry<String, Object> entry : room.getClients().entrySet()) {
                String username = entry.getKey();
                if (excludeUser == null || !username.equals(excludeUser)) {
                    ClientHandler clientHandler = (ClientHandler) entry.getValue();
                    clientHandler.sendMessage(message);
                }
            }
        }
    }
    
    public void sendPrivateMessage(String fromUser, String toUser, String content) {
        ClientHandler targetClient = connectedClients.get(toUser);
        ClientHandler senderClient = connectedClients.get(fromUser);
        
        if (targetClient != null) {
            Message privateMessage = new Message(fromUser, content, "", Message.MessageType.PRIVATE);
            targetClient.sendMessage(privateMessage.toString());
            if (senderClient != null) {
                senderClient.sendMessage("Private message sent to " + toUser + ": " + content);
            }
        } else {
            if (senderClient != null) {
                senderClient.sendMessage("User '" + toUser + "' not found or offline.");
            }
        }
    }
    
    public String getConnectedUsers() {
        StringBuilder users = new StringBuilder("Connected users: ");
        for (String username : connectedClients.keySet()) {
            users.append(username).append(" ");
        }
        return users.toString();
    }
    
    public String getAvailableRooms() {
        StringBuilder rooms = new StringBuilder("Available rooms: ");
        for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {
            rooms.append(entry.getKey())
                 .append("(").append(entry.getValue().getClientCount()).append(") ");
        }
        return rooms.toString();
    }
    
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        server.start();
    }
}