package com.chat.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Scanner scanner;
    private volatile boolean running;
    
    public ChatClient() {
        scanner = new Scanner(System.in);
        running = true;
    }
    
    public void start() {
        try {
            connectToServer();
            startMessageListener();
            handleUserInput();
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    private void connectToServer() throws IOException {
        System.out.println("Connecting to chat server...");
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connected to server at " + SERVER_HOST + ":" + SERVER_PORT);
    }
    
    private void startMessageListener() {
        Thread messageListener = new Thread(() -> {
            try {
                String message;
                while (running && (message = reader.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                if (running) {
                    System.err.println("Connection to server lost: " + e.getMessage());
                    running = false;
                }
            }
        });
        messageListener.setDaemon(true);
        messageListener.start();
    }
    
    private void handleUserInput() {
        System.out.println("You can start typing messages. Type '/help' for commands or '/quit' to exit.");
        
        while (running) {
            try {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    
                    if (input.isEmpty()) continue;
                    
                    if (input.equalsIgnoreCase("/quit") || input.equalsIgnoreCase("/exit")) {
                        running = false;
                        break;
                    }
                    
                    writer.println(input);
                }
            } catch (Exception e) {
                System.err.println("Error reading input: " + e.getMessage());
                break;
            }
        }
    }
    
    private void disconnect() {
        running = false;
        try {
            if (writer != null) {
                writer.println("/quit");
                writer.close();
            }
            if (reader != null) reader.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
        System.out.println("Disconnected from server.");
    }
    
    public static void main(String[] args) {
        System.out.println("=== Real-Time Chat Client ===");
        System.out.println("Developed using Java Socket Programming");
        System.out.println("=====================================");
        
        ChatClient client = new ChatClient();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(client::disconnect));
        
        client.start();
    }
}