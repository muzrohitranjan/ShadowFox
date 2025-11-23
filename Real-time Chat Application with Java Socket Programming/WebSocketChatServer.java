package com.chat.websocket;

import com.chat.common.ChatRoom;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.security.MessageDigest;
import java.util.Base64;

public class WebSocketChatServer {
    private static final int PORT = 8081;
    
    private ServerSocket serverSocket;
    private Map<String, ChatRoom> chatRooms;
    private Map<String, WebSocketClientHandler> connectedClients;
    private ExecutorService threadPool;
    private volatile boolean running;
    
    public WebSocketChatServer() {
        chatRooms = new ConcurrentHashMap<>();
        connectedClients = new ConcurrentHashMap<>();
        threadPool = Executors.newCachedThreadPool();
        chatRooms.put("general", new ChatRoom("general"));
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("WebSocket Chat Server started on port " + PORT);
            System.out.println("Open web/index.html in your browser");
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    WebSocketClientHandler handler = new WebSocketClientHandler(clientSocket, this);
                    threadPool.execute(handler);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
    
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            threadPool.shutdown();
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
    
    public synchronized void addClient(String username, WebSocketClientHandler handler) {
        connectedClients.put(username, handler);
        joinRoom(username, "general");
        System.out.println("Client connected: " + username);
    }
    
    public synchronized void removeClient(String username) {
        connectedClients.remove(username);
        System.out.println("Client disconnected: " + username);
    }
    
    public void joinRoom(String username, String roomName) {
        ChatRoom room = chatRooms.computeIfAbsent(roomName, ChatRoom::new);
        WebSocketClientHandler handler = connectedClients.get(username);
        
        if (handler != null) {
            room.addClient(username, handler);
            handler.setCurrentRoom(roomName);
        }
    }
    
    public void broadcastMessage(String roomName, String username, String content) {
        String message = String.format("{\"type\":\"message\",\"username\":\"%s\",\"content\":\"%s\"}", 
                                     username, content);
        ChatRoom room = chatRooms.get(roomName);
        if (room != null) {
            for (Map.Entry<String, Object> entry : room.getClients().entrySet()) {
                WebSocketClientHandler handler = (WebSocketClientHandler) entry.getValue();
                handler.sendMessage(message);
            }
        }
    }
    
    public static void main(String[] args) {
        new WebSocketChatServer().start();
    }
}

class WebSocketClientHandler implements Runnable {
    private Socket socket;
    private WebSocketChatServer server;
    private BufferedReader reader;
    private OutputStream output;
    private String username;
    private String currentRoom;
    private boolean handshakeComplete = false;
    
    public WebSocketClientHandler(Socket socket, WebSocketChatServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = socket.getOutputStream();
            
            if (performHandshake()) {
                handleMessages();
            }
        } catch (Exception e) {
            System.err.println("Handler error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    private boolean performHandshake() throws Exception {
        String line;
        String key = null;
        
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Sec-WebSocket-Key:")) {
                key = line.substring(18).trim();
            }
        }
        
        if (key == null) return false;
        
        String acceptKey = generateAcceptKey(key);
        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                         "Upgrade: websocket\r\n" +
                         "Connection: Upgrade\r\n" +
                         "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
        
        output.write(response.getBytes());
        output.flush();
        handshakeComplete = true;
        return true;
    }
    
    private String generateAcceptKey(String key) throws Exception {
        String concat = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(concat.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
    
    private void handleMessages() throws Exception {
        // Simplified message handling for demo
        sendMessage("{\"type\":\"system\",\"content\":\"Connected to WebSocket server\"}");
    }
    
    public void sendMessage(String message) {
        try {
            byte[] messageBytes = message.getBytes();
            byte[] frame = new byte[2 + messageBytes.length];
            frame[0] = (byte) 0x81; // Text frame
            frame[1] = (byte) messageBytes.length;
            System.arraycopy(messageBytes, 0, frame, 2, messageBytes.length);
            
            output.write(frame);
            output.flush();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String room) { this.currentRoom = room; }
    
    private void cleanup() {
        if (username != null) {
            server.removeClient(username);
        }
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
}