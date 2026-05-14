#pragma once

#include <memory>
#include <string>
#include "../models/User.hpp"

class Session;

class MessageHandler {
public:
    // Parse and handle an incoming message
    static void handle(const std::string& message, const User& user, std::shared_ptr<Session> session);

private:
    static void handleTextMessage(int conversationId, const std::string& content, const User& user, std::shared_ptr<Session> session);
    static void handleMediaMessage(const std::string& content, const std::string& mimeType, 
                                    const std::string& fileName, const User& user, std::shared_ptr<Session> session);
    static void handleGetConversations(const User& user, std::shared_ptr<Session> session);
    static void handleGetMessages(int conversationId, int limit, int beforeId, const User& user, std::shared_ptr<Session> session);
    static void handleGetUsers(const User& user, std::shared_ptr<Session> session);
    static void handleSendToUser(const std::string& otherRole, int otherId, const std::string& content, const User& user, std::shared_ptr<Session> session);
    static void handleNewConversation(const std::string& payload, const User& user, std::shared_ptr<Session> session);
};
