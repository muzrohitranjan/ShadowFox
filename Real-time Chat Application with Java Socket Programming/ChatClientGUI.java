package com.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClientGUI extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private volatile boolean connected = false;
    
    // GUI Components
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JTextField serverField;
    private JTextField portField;
    private JLabel statusLabel;
    
    public ChatClientGUI() {
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Real-Time Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // Create components
        createComponents();
        layoutComponents();
        addEventListeners();
        
        // Initial state
        updateConnectionState(false);
    }
    
    private void createComponents() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        chatArea.setBackground(Color.WHITE);
        
        messageField = new JTextField();
        sendButton = new JButton("Send");
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        
        serverField = new JTextField(SERVER_HOST, 10);
        portField = new JTextField(String.valueOf(SERVER_PORT), 5);
        statusLabel = new JLabel("Disconnected");
        statusLabel.setForeground(Color.RED);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Top panel - Connection controls
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Server:"));
        topPanel.add(serverField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(connectButton);
        topPanel.add(disconnectButton);
        topPanel.add(new JLabel("Status:"));
        topPanel.add(statusLabel);
        
        // Center panel - Chat area
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Bottom panel - Message input
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void addEventListeners() {
        connectButton.addActionListener(e -> connectToServer());
        disconnectButton.addActionListener(e -> disconnect());
        sendButton.addActionListener(e -> sendMessage());
        
        messageField.addActionListener(e -> sendMessage());
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });
    }
    
    private void connectToServer() {
        if (connected) return;
        
        try {
            String host = serverField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            connected = true;
            updateConnectionState(true);
            appendToChat("Connected to server at " + host + ":" + port);
            
            // Start message listener thread
            startMessageListener();
            
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to server: " + e.getMessage(), 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void startMessageListener() {
        Thread messageListener = new Thread(() -> {
            try {
                String message;
                while (connected && (message = reader.readLine()) != null) {
                    final String msg = message;
                    SwingUtilities.invokeLater(() -> appendToChat(msg));
                }
            } catch (IOException e) {
                if (connected) {
                    SwingUtilities.invokeLater(() -> {
                        appendToChat("Connection to server lost: " + e.getMessage());
                        disconnect();
                    });
                }
            }
        });
        messageListener.setDaemon(true);
        messageListener.start();
    }
    
    private void sendMessage() {
        if (!connected || writer == null) {
            JOptionPane.showMessageDialog(this, 
                "Not connected to server!", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;
        
        writer.println(message);
        messageField.setText("");
        
        // Handle quit command
        if (message.equalsIgnoreCase("/quit") || message.equalsIgnoreCase("/exit")) {
            disconnect();
        }
    }
    
    private void disconnect() {
        if (!connected) return;
        
        connected = false;
        
        try {
            if (writer != null) {
                writer.println("/quit");
                writer.close();
            }
            if (reader != null) reader.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
        
        updateConnectionState(false);
        appendToChat("Disconnected from server.");
    }
    
    private void updateConnectionState(boolean isConnected) {
        connected = isConnected;
        connectButton.setEnabled(!isConnected);
        disconnectButton.setEnabled(isConnected);
        sendButton.setEnabled(isConnected);
        messageField.setEnabled(isConnected);
        serverField.setEnabled(!isConnected);
        portField.setEnabled(!isConnected);
        
        if (isConnected) {
            statusLabel.setText("Connected");
            statusLabel.setForeground(Color.GREEN);
            messageField.requestFocus();
        } else {
            statusLabel.setText("Disconnected");
            statusLabel.setForeground(Color.RED);
        }
    }
    
    private void appendToChat(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatClientGUI().setVisible(true);
        });
    }
}