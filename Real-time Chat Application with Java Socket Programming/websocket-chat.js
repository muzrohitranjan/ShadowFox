class WebSocketChatClient {
    constructor() {
        this.ws = null;
        this.connected = false;
        this.username = '';
        this.currentRoom = 'general';
        
        this.initializeElements();
        this.attachEventListeners();
        this.updateConnectionStatus(false);
    }
    
    initializeElements() {
        this.connectionPanel = document.getElementById('connectionPanel');
        this.chatMain = document.getElementById('chatMain');
        this.connectBtn = document.getElementById('connectBtn');
        this.disconnectBtn = document.getElementById('disconnectBtn');
        this.joinChatBtn = document.getElementById('joinChat');
        this.connectionStatus = document.getElementById('connectionStatus');
        
        this.serverHost = document.getElementById('serverHost');
        this.serverPort = document.getElementById('serverPort');
        this.usernameInput = document.getElementById('username');
        
        this.chatMessages = document.getElementById('chatMessages');
        this.messageInput = document.getElementById('messageInput');
        this.sendBtn = document.getElementById('sendBtn');
        this.currentUserSpan = document.getElementById('currentUser');
        
        // Set WebSocket port
        this.serverPort.value = '8081';
    }
    
    attachEventListeners() {
        this.connectBtn.addEventListener('click', () => this.showConnectionPanel());
        this.disconnectBtn.addEventListener('click', () => this.disconnect());
        this.joinChatBtn.addEventListener('click', () => this.connect());
        
        this.sendBtn.addEventListener('click', () => this.sendMessage());
        this.messageInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.sendMessage();
        });
        
        this.usernameInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.connect();
        });
        
        document.querySelectorAll('.cmd-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const cmd = e.target.dataset.cmd;
                this.executeCommand(cmd);
            });
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
        
        try {
            this.ws = new WebSocket(`ws://${host}:${port}`);
            
            this.ws.onopen = () => {
                this.connected = true;
                this.updateConnectionStatus(true);
                this.connectionPanel.style.display = 'none';
                this.chatMain.style.display = 'flex';
                
                this.addSystemMessage('Connected to WebSocket server');
                this.addSystemMessage(`Welcome, ${this.username}!`);
                
                // Send join message
                this.ws.send(JSON.stringify({
                    type: 'join',
                    username: this.username,
                    room: this.currentRoom
                }));
                
                this.messageInput.focus();
            };
            
            this.ws.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    this.handleServerMessage(data);
                } catch (e) {
                    console.error('Error parsing message:', e);
                }
            };
            
            this.ws.onclose = () => {
                this.connected = false;
                this.updateConnectionStatus(false);
                this.addSystemMessage('Connection closed');
            };
            
            this.ws.onerror = (error) => {
                console.error('WebSocket error:', error);
                this.addSystemMessage('Connection error occurred');
            };
            
        } catch (error) {
            alert('Failed to connect: ' + error.message);
        }
    }
    
    disconnect() {
        if (this.ws) {
            this.ws.close();
        }
        this.connected = false;
        this.updateConnectionStatus(false);
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
            // Send message to server
            this.ws.send(JSON.stringify({
                type: 'message',
                username: this.username,
                content: message,
                room: this.currentRoom
            }));
            
            // Add to local chat
            this.addMessage(this.username, message, 'own');
        }
    }
    
    handleServerMessage(data) {
        switch (data.type) {
            case 'message':
                if (data.username !== this.username) {
                    this.addMessage(data.username, data.content, 'other');
                }
                break;
            case 'system':
                this.addSystemMessage(data.content);
                break;
            case 'private':
                this.addPrivateMessage(data.from, data.to, data.content, false);
                break;
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
            case '/users':
                this.addSystemMessage('Connected users: ' + this.username + ' (and others)');
                break;
            case '/rooms':
                this.addSystemMessage('Available rooms: general, tech, random');
                break;
            case '/quit':
                this.disconnect();
                break;
            default:
                this.addSystemMessage(`Unknown command: ${cmd}. Type /help for help.`);
        }
    }
    
    showHelp() {
        const helpText = `Available Commands:
/join <room>    - Join a chat room
/users          - List connected users  
/rooms          - List available rooms
/help           - Show this help
/quit           - Exit chat`;
        this.addSystemMessage(helpText);
    }
    
    joinRoom(roomName) {
        this.currentRoom = roomName;
        this.addSystemMessage(`Joined room: ${roomName}`);
        
        if (this.connected) {
            this.ws.send(JSON.stringify({
                type: 'join',
                username: this.username,
                room: roomName
            }));
        }
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
                <div class="message-header">${this.escapeHtml(username)}</div>
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
    
    scrollToBottom() {
        this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
    }
    
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Initialize WebSocket chat client
document.addEventListener('DOMContentLoaded', () => {
    new WebSocketChatClient();
});