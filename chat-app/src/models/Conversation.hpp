#pragma once
#include <string>
#include "picojson/picojson.h"

struct ConversationDTO {
    int         id              = 0;
    // other person's name for pair, group name for group
    std::string displayName;
    // "pair" | "group"
    std::string type;
    std::string lastMessage;
    std::string lastMessageType;
    std::string updatedAt;
    std::string toJson() const {
        picojson::object o;
        o["id"]                = picojson::value(static_cast<double>(id));
        o["display_name"]      = picojson::value(displayName);
        o["type"]              = picojson::value(type);
        o["last_message"]      = picojson::value(lastMessage);
        o["last_message_type"] = picojson::value(lastMessageType);
        o["updated_at"]        = picojson::value(updatedAt);
        return picojson::value(o).serialize();
    }
};
