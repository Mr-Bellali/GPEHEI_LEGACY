#pragma once
#include <string>
#include <vector>
#include "picojson/picojson.h"

// Incoming JSON frame sent by the client
struct ClientMessage {
    // "text" | "media" | "getConversations" | "newConversation"
    std::string event;
    std::string content;
    int         conversationId = 0;
    // only set when event == "media"
    std::string mimeType;
    std::string fileName;
    // only set when event == "newConversation"
    // "pair" | "group"
    std::string type;
    std::string name;
    // array of {userId, role} for participants
    struct Member { int userId = 0; std::string role; };
    std::vector<Member> members;
};

// Outgoing frame broadcast to conversation participants
struct MessageDTO {
    int         id             = 0;
    std::string content;
    // "text" | "media"
    std::string type;
    std::string mimeType;
    std::string fileName;
    int         senderId       = 0;
    std::string senderName;
    std::string senderRole;
    int         conversationId = 0;
    std::string createdAt;
    std::string toJson() const {
        picojson::object data;
        data["id"]              = picojson::value(static_cast<double>(id));
        data["content"]         = picojson::value(content);
        data["type"]            = picojson::value(type);
        data["mime_type"]       = picojson::value(mimeType);
        data["file_name"]       = picojson::value(fileName);
        data["sender_id"]       = picojson::value(static_cast<double>(senderId));
        data["sender_name"]     = picojson::value(senderName);
        data["sender_role"]     = picojson::value(senderRole);
        data["conversation_id"] = picojson::value(static_cast<double>(conversationId));
        data["created_at"]      = picojson::value(createdAt);
        picojson::object envelope;
        envelope["event"] = picojson::value(std::string("message"));
        envelope["data"]  = picojson::value(data);
        return picojson::value(envelope).serialize();
    }
};
