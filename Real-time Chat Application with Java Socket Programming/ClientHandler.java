package com.chat.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatServer server;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private String currentRoom;
    private volatile boolean running;
    
    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.running = true;
    }
    
    @Override
    public void run() {
        try {
            setupStreams();
            authenticateUser();
            handleMessages();
        } catch (IOException e) {
            System.err.println("Client handler error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    private void setupStreams() throws IOException {
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    
    private void authenticateUser() throws IOException {
        writer.println("Welcome to the Chat Server!");
        writer.println("Enter your username:");
        
        while (running) {
            String inputUsername = reader.readLine();
            if (inputUsername == null) {
                return;
            }
            
            inputUsername = inputUsername.trim();
            if (inputUsername.isEmpty()) {
                writer.println("Username cannot be empty. Please try again:");
                continue;
            }
            
            if (inputUsername.contains(" ") || inputUsername.length() > 20) {
                writer.println("Username must be single word and max 20 characters. Please try again:");
                continue;
            }
            
            // Check if username is already taken
            if (isUsernameTaken(inputUsername)) {
                writer.println("Username '" + inputUsername + "' is already taken. Please choose another:");
                continue;
            }
            
            this.username = inputUsername;
            server.addClient(username, this);
            writer.println("Welcome, " + username + "! You are now connected.");
            writer.println("Type '/help' for available commands.");
            break;
        }
    }
    
    private boolean isUsernameTaken(String username) {
        // This would be implemented by checking with the server
        // For now, we'll assume it's handled by the server's addClient method
        return false;
    }
    
    private void handleMessages() throws IOException {
        String message;
        while (running && (message = reader.readLine()) != null) {
            message = message.trim();
            if (message.isEmpty()) continue;
            
            if (message.startsWith("/")) {
                handleCommand(message);
            } else {
                if (currentRoom != null) {
                    server.broadcastMessage(currentRoom, username, message);
                } else {
                    writer.println("You are not in any room. Use '/join <room>' to join a room.");
                }
            }
        }
    }
    
    private void handleCommand(String command) {
        String[] parts = command.split(" ", 3);
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "/help":
                sendHelpMessage();
                break;
                
            case "/join":
                if (parts.length < 2) {
                    writer.println("Usage: /join <room_name>");
                } else {
                    server.joinRoom(username, parts[1]);
                }
                break;
                
            case "/leave":
                if (currentRoom != null) {
                    server.leaveRoom(username, currentRoom);
                    currentRoom = null;
                    writer.println("You left the room.");
                } else {
                    writer.println("You are not in any room.");
                }
                break;
                
            case "/pm":
            case "/private":
                if (parts.length < 3) {
                    writer.println("Usage: /pm <username> <message>");
                } else {
                    server.sendPrivateMessage(username, parts[1], parts[2]);
                }
                break;
                
            case "/users":
                writer.println(server.getConnectedUsers());
                break;
                
            case "/rooms":
                writer.println(server.getAvailableRooms());
                break;
                
            case "/quit":
            case "/exit":
                writer.println("Goodbye!");
                running = false;
                break;
                
            default:
                writer.println("Unknown command. Type '/help' for available commands.");
        }
    }
    
    private void sendHelpMessage() {
        writer.println("=== Available Commands ===");
        writer.println("/join <room>    - Join a chat room");
        writer.println("/leave          - Leave current room");
        writer.println("/pm <user> <msg> - Send private message");
        writer.println("/users          - List connected users");
        writer.println("/rooms          - List available rooms");
        writer.println("/help           - Show this help message");
        writer.println("/quit           - Exit the chat");
        writer.println("========================");
    }
    
    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(String room) {
        this.currentRoom = room;
    }
    
    private void cleanup() {
        running = false;
        if (username != null) {
            server.removeClient(username);
        }
        
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}