#pragma once

#include <string>
#include <vector>
#include "../models/User.hpp"
#include "../models/Conversation.hpp"
#include "../models/Message.hpp"

class ConversationService {
public:
    static std::vector<ConversationDTO> getConversationsForUser(const User& user);

    static std::vector<MessageDTO> getMessages(const User& user, int conversationId, int limit = 50, int beforeId = 0);

    static MessageDTO saveTextMessage(const User& user, int conversationId, const std::string& content);

    static std::vector<std::string> getConversationMemberSessionKeys(int conversationId);

    static int findPairConversation(const User& user, const std::string& otherRole, int otherId);
    static int createPairConversation(const User& user, const std::string& otherRole, int otherId);

    struct UserSuggestion {
        int id = 0;
        std::string role;
        std::string firstName;
        std::string lastName;
        std::string email;
        int conversationId = 0;
    };

    static std::vector<UserSuggestion> getAllUsersWithConversationHint(const User& user);
};
