class ChatClient {
    constructor() {
        this.socket = null;
        this.connected = false;
        this.username = '';
        this.currentRoom = 'general';
        this.users = new Set();
        this.rooms = new Set(['general']);
        
        this.initializeElements();
        this.attachEventListeners();
        this.updateConnectionStatus(false);
    }
    
    initializeElements() {
        // Connection elements
        this.connectionPanel = document.getElementById('connectionPanel');
        this.chatMain = document.getElementById('chatMain');
        this.connectBtn = document.getElementById('connectBtn');
        this.disconnectBtn = document.getElementById('disconnectBtn');
        this.joinChatBtn = document.getElementById('joinChat');
        this.connectionStatus = document.getElementById('connectionStatus');
        
        // Form elements
        this.serverHost = document.getElementById('serverHost');
        this.serverPort = document.getElementById('serverPort');
        this.usernameInput = document.getElementById('username');
        
        // Chat elements
        this.chatMessages = document.getElementById('chatMessages');
        this.messageInput = document.getElementById('messageInput');
        this.sendBtn = document.getElementById('sendBtn');
        this.currentUserSpan = document.getElementById('currentUser');
        
        // Sidebar elements
        this.roomList = document.getElementById('roomList');
        this.userList = document.getElementById('userList');
        this.newRoomName = document.getElementById('newRoomName');
        this.createRoomBtn = document.getElementById('createRoom');
        
        // Modal elements
        this.pmModal = document.getElementById('pmModal');
        this.closePM = document.getElementById('closePM');
        this.pmUser = document.getElementById('pmUser');
        this.pmMessage = document.getElementById('pmMessage');
        this.sendPMBtn = document.getElementById('sendPM');
        this.cancelPMBtn = document.getElementById('cancelPM');
    }
    
    attachEventListeners() {
        // Connection events
        this.connectBtn.addEventListener('click', () => this.showConnectionPanel());
        this.disconnectBtn.addEventListener('click', () => this.disconnect());
        this.joinChatBtn.addEventListener('click', () => this.connect());
        
        // Message events
        this.sendBtn.addEventListener('click', () => this.sendMessage());
        this.messageInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.sendMessage();
        });
        
        // Room events
        this.createRoomBtn.addEventListener('click', () => this.createRoom());
        this.newRoomName.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.createRoom();
        });
        
        // Command buttons
        document.querySelectorAll('.cmd-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const cmd = e.target.dataset.cmd;
                this.executeCommand(cmd);
            });
        });
        
        // Modal events
        this.closePM.addEventListener('click', () => this.closePMModal());
        this.sendPMBtn.addEventListener('click', () => this.sendPrivateMessage());
        this.cancelPMBtn.addEventListener('click', () => this.closePMModal());
        
        // Click outside modal to close
        this.pmModal.addEventListener('click', (e) => {
            if (e.target === this.pmModal) this.closePMModal();
        });
        
        // Username input validation
        this.usernameInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.connect();
        });
    }
    
    showConnectionPanel() {
        this.connectionPanel.style.display = 'flex';
        this.chatMain.style.display = 'none';
        this.usernameInput.focus();
    }
    
    connect() {
        const host = this.serverHost.value.trim();
        const port = this.serverPort.value.trim();
        const username = this.usernameInput.value.trim();
        
        if (!username) {
            alert('Please enter a username');
            return;
        }
        
        if (username.length > 20 || username.includes(' ')) {
            alert('Username must be single word and max 20 characters');
            return;
        }
        
        this.username = username;
        this.currentUserSpan.textContent = username;
        
        // Simulate WebSocket connection (since we can't directly connect to Java Socket)
        this.simulateConnection();
    }
    
    simulateConnection() {
        // Since we can't directly connect to Java Socket from browser,
        // we'll simulate the connection for demo purposes
        setTimeout(() => {
            this.connected = true;
            this.updateConnectionStatus(true);
            this.connectionPanel.style.display = 'none';
            this.chatMain.style.display = 'flex';
            
            this.addSystemMessage('Connected to chat server');
            this.addSystemMessage(`Welcome, ${this.username}! You joined room: ${this.currentRoom}`);
            this.addSystemMessage('Type /help for available commands');
            
            // Simulate some users
            this.users.add(this.username);
            this.users.add('Alice');
            this.users.add('Bob');
            this.updateUserList();
            
            this.messageInput.focus();
        }, 1000);
    }
    
    disconnect() {
        this.connected = false;
        this.updateConnectionStatus(false);
        this.addSystemMessage('Disconnected from server');
        this.users.clear();
        this.updateUserList();
    }
    
    updateConnectionStatus(connected) {
        this.connected = connected;
        this.connectionStatus.textContent = connected ? 'Connected' : 'Disconnected';
        this.connectionStatus.className = connected ? 'status connected' : 'status disconnected';
        this.connectBtn.disabled = connected;
        this.disconnectBtn.disabled = !connected;
    }
    
    sendMessage() {
        if (!this.connected) {
            alert('Not connected to server!');
            return;
        }
        
        const message = this.messageInput.value.trim();
        if (!message) return;
        
        this.messageInput.value = '';
        
        if (message.startsWith('/')) {
            this.executeCommand(message);
        } else {
            this.addMessage(this.username, message, 'own');
            // Simulate echo from other users
            setTimeout(() => {
                const responses = [
                    'That\'s interesting!',
                    'I agree with you',
                    'Good point!',
                    'Thanks for sharing',
                    'Nice!'
                ];
                const randomUser = Array.from(this.users).filter(u => u !== this.username)[0];
                if (randomUser && Math.random() > 0.5) {
                    const randomResponse = responses[Math.floor(Math.random() * responses.length)];
                    this.addMessage(randomUser, randomResponse, 'other');
                }
            }, 1000 + Math.random() * 2000);
        }
    }
    
    executeCommand(command) {
        const parts = command.split(' ');
        const cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case '/help':
                this.showHelp();
                break;
            case '/join':
                if (parts.length > 1) {
                    this.joinRoom(parts[1]);
                } else {
                    this.addSystemMessage('Usage: /join <room_name>');
                }
                break;
            case '/leave':
                this.leaveRoom();
                break;
            case '/pm':
            case '/private':
                if (parts.length > 2) {
                    this.openPMModal(parts[1]);
                } else {
                    this.openPMModal();
                }
                break;
            case '/users':
                this.listUsers();
                break;
            case '/rooms':
                this.listRooms();
                break;
            case '/quit':
            case '/exit':
                this.disconnect();
                break;
            default:
                this.addSystemMessage(`Unknown command: ${cmd}. Type /help for available commands.`);
        }
    }
    
    showHelp() {
        const helpText = `
Available Commands:
/join <room>    - Join a chat room
/leave          - Leave current room
/pm <user>      - Send private message
/users          - List connected users
/rooms          - List available rooms
/help           - Show this help message
/quit           - Exit the chat
        `.trim();
        this.addSystemMessage(helpText);
    }
    
    joinRoom(roomName) {
        if (roomName === this.currentRoom) {
            this.addSystemMessage(`You are already in room: ${roomName}`);
            return;
        }
        
        this.addSystemMessage(`Left room: ${this.currentRoom}`);
        this.currentRoom = roomName;
        this.rooms.add(roomName);
        this.addSystemMessage(`Joined room: ${roomName}`);
        this.updateRoomList();
        this.clearMessages();
    }
    
    leaveRoom() {
        this.addSystemMessage(`Left room: ${this.currentRoom}`);
        this.currentRoom = 'general';
        this.addSystemMessage(`Joined room: ${this.currentRoom}`);
        this.updateRoomList();
    }
    
    createRoom() {
        const roomName = this.newRoomName.value.trim();
        if (!roomName) return;
        
        this.newRoomName.value = '';
        this.joinRoom(roomName);
    }
    
    openPMModal(username = '') {
        this.pmUser.value = username;
        this.pmMessage.value = '';
        this.pmModal.style.display = 'block';
        if (username) {
            this.pmMessage.focus();
        } else {
            this.pmUser.focus();
        }
    }
    
    closePMModal() {
        this.pmModal.style.display = 'none';
    }
    
    sendPrivateMessage() {
        const user = this.pmUser.value.trim();
        const message = this.pmMessage.value.trim();
        
        if (!user || !message) {
            alert('Please enter both username and message');
            return;
        }
        
        if (!this.users.has(user)) {
            alert(`User '${user}' not found`);
            return;
        }
        
        this.addPrivateMessage(this.username, user, message, true);
        this.closePMModal();
        
        // Simulate response
        setTimeout(() => {
            this.addPrivateMessage(user, this.username, 'Thanks for your message!', false);
        }, 1000);
    }
    
    listUsers() {
        const userList = Array.from(this.users).join(', ');
        this.addSystemMessage(`Connected users: ${userList}`);
    }
    
    listRooms() {
        const roomList = Array.from(this.rooms).map(room => 
            room === this.currentRoom ? `${room} (current)` : room
        ).join(', ');
        this.addSystemMessage(`Available rooms: ${roomList}`);
    }
    
    addMessage(username, content, type = 'other') {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${type}`;
        
        const time = new Date().toLocaleTimeString('en-US', { 
            hour12: false, 
            hour: '2-digit', 
            minute: '2-digit' 
        });
        
        if (type !== 'own') {
            messageDiv.innerHTML = `
                <div class="message-header">${username}</div>
                <div class="message-content">${this.escapeHtml(content)}</div>
                <div class="message-time">${time}</div>
            `;
        } else {
            messageDiv.innerHTML = `
                <div class="message-content">${this.escapeHtml(content)}</div>
                <div class="message-time">${time}</div>
            `;
        }
        
        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();
    }
    
    addSystemMessage(content) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message system';
        messageDiv.innerHTML = `
            <div class="message-content">${this.escapeHtml(content)}</div>
        `;
        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();
    }
    
    addPrivateMessage(from, to, content, sent) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message private';
        
        const time = new Date().toLocaleTimeString('en-US', { 
            hour12: false, 
            hour: '2-digit', 
            minute: '2-digit' 
        });
        
        const header = sent ? `Private to ${to}` : `Private from ${from}`;
        messageDiv.innerHTML = `
            <div class="message-header">${header}</div>
            <div class="message-content">${this.escapeHtml(content)}</div>
            <div class="message-time">${time}</div>
        `;
        
        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();
    }
    
    updateRoomList() {
        this.roomList.innerHTML = '';
        this.rooms.forEach(room => {
            const roomDiv = document.createElement('div');
            roomDiv.className = `room-item ${room === this.currentRoom ? 'active' : ''}`;
            roomDiv.dataset.room = room;
            roomDiv.innerHTML = `<i class="fas fa-hashtag"></i> ${room}`;
            roomDiv.addEventListener('click', () => this.joinRoom(room));
            this.roomList.appendChild(roomDiv);
        });
    }
    
    updateUserList() {
        this.userList.innerHTML = '';
        this.users.forEach(user => {
            const userDiv = document.createElement('div');
            userDiv.className = 'user-item';
            userDiv.innerHTML = `
                <i class="fas fa-circle"></i>
                ${user}
                ${user === this.username ? ' (You)' : ''}
            `;
            if (user !== this.username) {
                userDiv.addEventListener('click', () => this.openPMModal(user));
                userDiv.style.cursor = 'pointer';
                userDiv.title = 'Click to send private message';
            }
            this.userList.appendChild(userDiv);
        });
    }
    
    clearMessages() {
        this.chatMessages.innerHTML = '';
    }
    
    scrollToBottom() {
        this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
    }
    
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Initialize chat client when page loads
document.addEventListener('DOMContentLoaded', () => {
    new ChatClient();
});