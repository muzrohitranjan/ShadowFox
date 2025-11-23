package com.chat.test;

import com.chat.server.ChatServer;
import com.chat.client.ChatClient;

/**
 * Demo class to showcase the chat application features
 * This class demonstrates various functionalities of the chat system
 */
public class ChatApplicationDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Real-Time Chat Application Demo ===");
        System.out.println("This demo showcases the following features:");
        System.out.println("1. Multi-user real-time messaging");
        System.out.println("2. Chat room functionality");
        System.out.println("3. Private messaging");
        System.out.println("4. Command system");
        System.out.println("5. Thread-safe operations");
        System.out.println();
        
        printInstructions();
    }
    
    private static void printInstructions() {
        System.out.println("=== How to Run the Demo ===");
        System.out.println();
        System.out.println("Step 1: Start the Server");
        System.out.println("  - Run: java com.chat.server.ChatServer");
        System.out.println("  - Or use: run-server.bat");
        System.out.println();
        System.out.println("Step 2: Start Multiple Clients");
        System.out.println("  - Console Client: java com.chat.client.ChatClient");
        System.out.println("  - GUI Client: java com.chat.client.ChatClientGUI");
        System.out.println("  - Or use: run-client.bat / run-gui-client.bat");
        System.out.println();
        System.out.println("Step 3: Test Features");
        System.out.println("  - Enter different usernames for each client");
        System.out.println("  - Join the same room: /join general");
        System.out.println("  - Send messages and see real-time updates");
        System.out.println("  - Try private messages: /pm username message");
        System.out.println("  - List users: /users");
        System.out.println("  - List rooms: /rooms");
        System.out.println();
        System.out.println("=== Sample Test Scenario ===");
        System.out.println("1. Start server");
        System.out.println("2. Connect Client A (username: alice)");
        System.out.println("3. Connect Client B (username: bob)");
        System.out.println("4. Both join 'general' room");
        System.out.println("5. Alice sends: Hello everyone!");
        System.out.println("6. Bob receives message instantly");
        System.out.println("7. Bob sends private message: /pm alice Hi Alice!");
        System.out.println("8. Test room switching: /join tech-talk");
        System.out.println("9. Verify message isolation between rooms");
        System.out.println();
        System.out.println("=== Performance Testing ===");
        System.out.println("- Connect 10+ clients simultaneously");
        System.out.println("- Send rapid messages to test throughput");
        System.out.println("- Test client disconnection/reconnection");
        System.out.println("- Monitor server resource usage");
        System.out.println();
        System.out.println("=== Advanced Features to Test ===");
        System.out.println("- Message history when joining rooms");
        System.out.println("- Graceful handling of client disconnects");
        System.out.println("- Server shutdown with active clients");
        System.out.println("- Invalid command handling");
        System.out.println("- Username validation and duplicates");
        System.out.println();
        System.out.println("Happy Testing! 🚀");
    }
}