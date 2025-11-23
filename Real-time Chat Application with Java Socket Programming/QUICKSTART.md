# Quick Start Guide - Real-Time Chat Application

## 🚀 Get Started in 3 Minutes

### Option 1: Using Batch Scripts (Easiest)

1. **Start Server**
   ```
   Double-click: run-server.bat
   ```

2. **Start Client(s)**
   ```
   Double-click: run-client.bat (Console)
   OR
   Double-click: run-gui-client.bat (GUI)
   ```

### Option 2: Command Line

1. **Compile & Run Server**
   ```bash
   cd ChatApplication/src/main/java
   javac com/chat/**/*.java
   java com.chat.server.ChatServer
   ```

2. **Run Client (New Terminal)**
   ```bash
   cd ChatApplication/src/main/java
   java com.chat.client.ChatClient
   ```

### Option 3: Using Maven

1. **Start Server**
   ```bash
   mvn compile exec:java@run-server
   ```

2. **Start Client**
   ```bash
   mvn compile exec:java@run-client
   ```

## 💬 First Chat Session

1. **Server Output:**
   ```
   Chat Server started on port 8080
   Waiting for clients to connect...
   ```

2. **Client Steps:**
   - Enter username when prompted
   - You'll automatically join 'general' room
   - Start typing messages!

3. **Try Commands:**
   ```
   /help              - Show commands
   /join tech         - Join 'tech' room
   /pm alice Hello!   - Private message to alice
   /users             - List online users
   /quit              - Exit
   ```

## 🧪 Test Scenario

1. Start server
2. Open 2-3 client windows
3. Use different usernames (alice, bob, charlie)
4. All join same room: `/join general`
5. Send messages and see real-time sync
6. Try private messaging: `/pm bob Hi there!`
7. Create new room: `/join developers`

## ⚡ Features Showcase

- **Real-time messaging** - Messages appear instantly
- **Multiple rooms** - Switch between different chat rooms
- **Private messages** - Direct user-to-user communication
- **User management** - See who's online
- **Message history** - Previous messages when joining rooms
- **GUI option** - User-friendly graphical interface

## 🔧 Troubleshooting

**Port 8080 in use?**
- Change PORT in ChatServer.java
- Or kill process using port 8080

**Can't connect?**
- Ensure server is running first
- Check firewall settings
- Try localhost or 127.0.0.1

**Compilation errors?**
- Ensure Java 17+ is installed
- Check JAVA_HOME environment variable

## 📱 What's Next?

After testing basic functionality:
- Try the GUI client for better experience
- Test with multiple users
- Explore all chat commands
- Check the full README.md for advanced features

**Enjoy your real-time chat experience! 🎉**