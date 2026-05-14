#include "MessageHandler.hpp"
#include "Session.hpp"
#include "SessionManager.hpp"
#include "ConversationService.hpp"
#include "../models/Message.hpp"
#include "../db/Database.hpp"
#include "picojson/picojson.h"
#include <iostream>

void MessageHandler::handle(const std::string& message, const User& user, std::shared_ptr<Session> session) {
    try {
        picojson::value v;
        std::string err = picojson::parse(v, message);
        if (!err.empty()) {
            throw std::runtime_error("JSON parse error: " + err);
        }

        if (!v.is<picojson::object>()) {
            throw std::runtime_error("Message must be a JSON object");
        }

        auto& obj = v.get<picojson::object>();
        
        if (!obj.count("event")) {
            throw std::runtime_error("Missing 'event' field");
        }
        
        std::string event = obj["event"].get<std::string>();

        if (event == "text") {
            if (!obj.count("content") || !obj.count("conversation_id")) {
                throw std::runtime_error("Missing required fields for text message");
            }
            std::string content = obj["content"].get<std::string>();
            int conversationId = static_cast<int>(obj["conversation_id"].get<double>());
            handleTextMessage(conversationId, content, user, session);
        } else if (event == "media") {
            if (!obj.count("content") || !obj.count("mime_type") || !obj.count("file_name") || !obj.count("conversation_id")) {
                throw std::runtime_error("Missing required fields for media message");
            }
            std::string content = obj["content"].get<std::string>();
            std::string mimeType = obj["mime_type"].get<std::string>();
            std::string fileName = obj["file_name"].get<std::string>();
            int conversationId = static_cast<int>(obj["conversation_id"].get<double>());
            handleMediaMessage(content, mimeType, fileName, user, session);
        } else if (event == "getConversations") {
            handleGetConversations(user, session);
        } else if (event == "getMessages") {
            if (!obj.count("conversation_id")) {
                throw std::runtime_error("Missing required fields for getMessages");
            }
            int conversationId = static_cast<int>(obj["conversation_id"].get<double>());
            int limit = obj.count("limit") ? static_cast<int>(obj["limit"].get<double>()) : 50;
            int beforeId = obj.count("before_id") ? static_cast<int>(obj["before_id"].get<double>()) : 0;
            handleGetMessages(conversationId, limit, beforeId, user, session);
        } else if (event == "getUsers") {
            handleGetUsers(user, session);
        } else if (event == "sendToUser") {
            if (!obj.count("to_user_id") || !obj.count("to_role") || !obj.count("content")) {
                throw std::runtime_error("Missing required fields for sendToUser");
            }
            int toUserId = static_cast<int>(obj["to_user_id"].get<double>());
            std::string toRole = obj["to_role"].get<std::string>();
            std::string content = obj["content"].get<std::string>();
            handleSendToUser(toRole, toUserId, content, user, session);
        } else if (event == "newConversation") {
            handleNewConversation(message, user, session);
        } else {
            throw std::runtime_error("Unknown event: " + event);
        }
    } catch (const std::exception& e) {
        std::cerr << "MessageHandler error: " << e.what() << std::endl;
    }
}

void MessageHandler::handleTextMessage(int conversationId, const std::string& content, const User& user, std::shared_ptr<Session> session) {
    std::cout << "Text message from " << user.fullName() << ": " << content << std::endl;
    auto msg = ConversationService::saveTextMessage(user, conversationId, content);
    if (msg.id == 0) {
        return;
    }

    auto keys = ConversationService::getConversationMemberSessionKeys(conversationId);
    std::string payload = msg.toJson();
    for (const auto& key : keys) {
        SessionManager::instance().sendTo(key, payload);
    }

    for (const auto& key : keys) {
        auto s = SessionManager::instance().getSession(key);
        if (!s) continue;
        auto conversations = ConversationService::getConversationsForUser(s->user());
        picojson::array arr;
        arr.reserve(conversations.size());
        for (const auto& c : conversations) {
            picojson::object o;
            o["id"] = picojson::value(static_cast<double>(c.id));
            o["display_name"] = picojson::value(c.displayName);
            o["type"] = picojson::value(c.type);
            o["last_message"] = picojson::value(c.lastMessage);
            o["last_message_type"] = picojson::value(c.lastMessageType);
            o["updated_at"] = picojson::value(c.updatedAt);
            arr.push_back(picojson::value(o));
        }
        picojson::object envelope;
        envelope["event"] = picojson::value(std::string("conversations"));
        envelope["data"] = picojson::value(arr);
        s->send(picojson::value(envelope).serialize());
    }
}

void MessageHandler::handleMediaMessage(const std::string& content, const std::string& mimeType,
                                         const std::string& fileName, const User& user, std::shared_ptr<Session> session) {
    std::cout << "Media message from " << user.fullName() << ": " << fileName << " (" << mimeType << ")" << std::endl;
    // TODO: Save file and metadata to database, broadcast to conversation participants
}

void MessageHandler::handleGetConversations(const User& user, std::shared_ptr<Session> session) {
    std::cout << "Getting conversations for " << user.fullName() << std::endl;
    auto conversations = ConversationService::getConversationsForUser(user);

    picojson::array arr;
    arr.reserve(conversations.size());
    for (const auto& c : conversations) {
        picojson::object o;
        o["id"] = picojson::value(static_cast<double>(c.id));
        o["display_name"] = picojson::value(c.displayName);
        o["type"] = picojson::value(c.type);
        o["last_message"] = picojson::value(c.lastMessage);
        o["last_message_type"] = picojson::value(c.lastMessageType);
        o["updated_at"] = picojson::value(c.updatedAt);
        arr.push_back(picojson::value(o));
    }

    picojson::object envelope;
    envelope["event"] = picojson::value(std::string("conversations"));
    envelope["data"] = picojson::value(arr);
    session->send(picojson::value(envelope).serialize());
}

void MessageHandler::handleGetMessages(int conversationId, int limit, int beforeId, const User& user, std::shared_ptr<Session> session) {
    auto messages = ConversationService::getMessages(user, conversationId, limit, beforeId);
    picojson::array arr;
    arr.reserve(messages.size());
    for (const auto& m : messages) {
        picojson::object o;
        o["id"] = picojson::value(static_cast<double>(m.id));
        o["content"] = picojson::value(m.content);
        o["type"] = picojson::value(m.type);
        o["mime_type"] = picojson::value(m.mimeType);
        o["file_name"] = picojson::value(m.fileName);
        o["sender_id"] = picojson::value(static_cast<double>(m.senderId));
        o["sender_name"] = picojson::value(m.senderName);
        o["sender_role"] = picojson::value(m.senderRole);
        o["conversation_id"] = picojson::value(static_cast<double>(m.conversationId));
        o["created_at"] = picojson::value(m.createdAt);
        arr.push_back(picojson::value(o));
    }

    picojson::object data;
    data["conversation_id"] = picojson::value(static_cast<double>(conversationId));
    data["messages"] = picojson::value(arr);

    picojson::object envelope;
    envelope["event"] = picojson::value(std::string("messages"));
    envelope["data"] = picojson::value(data);
    session->send(picojson::value(envelope).serialize());
}

void MessageHandler::handleGetUsers(const User& user, std::shared_ptr<Session> session) {
    auto users = ConversationService::getAllUsersWithConversationHint(user);
    picojson::array arr;
    arr.reserve(users.size());
    for (const auto& u : users) {
        picojson::object o;
        o["id"] = picojson::value(static_cast<double>(u.id));
        o["role"] = picojson::value(u.role);
        o["first_name"] = picojson::value(u.firstName);
        o["last_name"] = picojson::value(u.lastName);
        o["email"] = picojson::value(u.email);
        o["conversation_id"] = picojson::value(static_cast<double>(u.conversationId));
        arr.push_back(picojson::value(o));
    }
    picojson::object envelope;
    envelope["event"] = picojson::value(std::string("users"));
    envelope["data"] = picojson::value(arr);
    session->send(picojson::value(envelope).serialize());
}

void MessageHandler::handleSendToUser(const std::string& otherRole, int otherId, const std::string& content, const User& user, std::shared_ptr<Session> session) {
    int conversationId = ConversationService::findPairConversation(user, otherRole, otherId);
    if (conversationId == 0) {
        conversationId = ConversationService::createPairConversation(user, otherRole, otherId);
    }
    if (conversationId == 0) return;

    auto msg = ConversationService::saveTextMessage(user, conversationId, content);
    if (msg.id == 0) return;

    auto keys = ConversationService::getConversationMemberSessionKeys(conversationId);
    std::string payload = msg.toJson();
    for (const auto& key : keys) {
        SessionManager::instance().sendTo(key, payload);
    }

    for (const auto& key : keys) {
        auto s = SessionManager::instance().getSession(key);
        if (!s) continue;
        auto conversations = ConversationService::getConversationsForUser(s->user());
        picojson::array arr;
        arr.reserve(conversations.size());
        for (const auto& c : conversations) {
            picojson::object o;
            o["id"] = picojson::value(static_cast<double>(c.id));
            o["display_name"] = picojson::value(c.displayName);
            o["type"] = picojson::value(c.type);
            o["last_message"] = picojson::value(c.lastMessage);
            o["last_message_type"] = picojson::value(c.lastMessageType);
            o["updated_at"] = picojson::value(c.updatedAt);
            arr.push_back(picojson::value(o));
        }
        picojson::object envelope;
        envelope["event"] = picojson::value(std::string("conversations"));
        envelope["data"] = picojson::value(arr);
        s->send(picojson::value(envelope).serialize());

        auto users = ConversationService::getAllUsersWithConversationHint(s->user());
        picojson::array uarr;
        uarr.reserve(users.size());
        for (const auto& u : users) {
            picojson::object o;
            o["id"] = picojson::value(static_cast<double>(u.id));
            o["role"] = picojson::value(u.role);
            o["first_name"] = picojson::value(u.firstName);
            o["last_name"] = picojson::value(u.lastName);
            o["email"] = picojson::value(u.email);
            o["conversation_id"] = picojson::value(static_cast<double>(u.conversationId));
            uarr.push_back(picojson::value(o));
        }
        picojson::object uenv;
        uenv["event"] = picojson::value(std::string("users"));
        uenv["data"] = picojson::value(uarr);
        s->send(picojson::value(uenv).serialize());
    }
}

void MessageHandler::handleNewConversation(const std::string& payload, const User& user, std::shared_ptr<Session> session) {
    std::cout << "Creating new conversation initiated by " << user.fullName() << std::endl;
    // TODO: Parse payload, create conversation, notify participants
}
