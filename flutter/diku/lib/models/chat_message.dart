class ChatMessage {
  final String sender;
  final String message;

  ChatMessage({required this.sender, required this.message});

  factory ChatMessage.fromMap(Map<String, String> map) {
    return ChatMessage(
      sender: map['sender'] ?? 'bot',
      message: map['message'] ?? '',
    );
  }

  Map<String, String> toMap() {
    return {
      'sender': sender,
      'message': message,
    };
  }
}
