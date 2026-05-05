package model;

import java.time.LocalDateTime;

public class Message {

    private int id;

    private int conversationId;

    // Sender (polymorphic: Student or Teacher)
    private SenderType senderType;
    private int senderId;

    private String content;

    private MessageStatus status;

    private LocalDateTime sentAt;
    private LocalDateTime readAt; // nullable

    // Constructors
    public Message() {}

    public Message(int id, int conversationId,
                   SenderType senderType, int senderId,
                   String content, MessageStatus status,
                   LocalDateTime sentAt, LocalDateTime readAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.senderId = senderId;
        this.content = content;
        this.status = status;
        this.sentAt = sentAt;
        this.readAt = readAt;
    }

    // Getters & Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getConversationId() { return conversationId; }
    public void setConversationId(int conversationId) { this.conversationId = conversationId; }

    public SenderType getSenderType() { return senderType; }
    public void setSenderType(SenderType senderType) { this.senderType = senderType; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}