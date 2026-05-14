#include "ConversationService.hpp"
#include "../db/Database.hpp"
#include <iostream>
#include <algorithm>
#include <map>
#include <chrono>
#include <iomanip>
#include <sstream>

// Helper: current ISO 8601 timestamp
static std::string now_iso8601() {
    auto now = std::chrono::system_clock::now();
    auto time = std::chrono::system_clock::to_time_t(now);
    std::stringstream ss;
    ss << std::put_time(std::gmtime(&time), "%Y-%m-%dT%H:%M:%SZ");
    return ss.str();
}

static bool is_member(const User& user, int conversationId) {
    Database db;
    std::string where;
    if (user.role == "teacher") {
        where = "teacher_id = " + std::to_string(user.id);
    } else if (user.role == "student") {
        where = "student_id = " + std::to_string(user.id);
    } else {
        return false;
    }

    auto rows = db.query(
        "SELECT 1 FROM conversation_participant WHERE conversation_id = " + std::to_string(conversationId) +
        " AND " + where + " LIMIT 1"
    );
    return !rows.empty();
}

static std::string participantWhere(const std::string& role, int id) {
    if (role == "teacher") {
        return "teacher_id = " + std::to_string(id);
    }
    if (role == "student") {
        return "student_id = " + std::to_string(id);
    }
    return "1=0";
}

static std::string participantWhereAlias(const std::string& role, int id, const std::string& alias) {
    if (role == "teacher") {
        return alias + ".teacher_id = " + std::to_string(id);
    }
    if (role == "student") {
        return alias + ".student_id = " + std::to_string(id);
    }
    return "1=0";
}

int ConversationService::findPairConversation(const User& user, const std::string& otherRole, int otherId) {
    try {
        Database db;
        std::string selfWhere = participantWhereAlias(user.role, user.id, "cp1");
        std::string otherWhere = participantWhereAlias(otherRole, otherId, "cp2");
        std::string query =
            "SELECT c.id "
            "FROM conversation c "
            "JOIN conversation_participant cp1 ON cp1.conversation_id = c.id "
            "JOIN conversation_participant cp2 ON cp2.conversation_id = c.id "
            "WHERE c.type_conv = 'pair' "
            "AND " + selfWhere + " "
            "AND " + otherWhere + " "
            "LIMIT 1";
        auto rows = db.query(query);
        if (!rows.empty() && !rows[0].empty()) {
            return std::stoi(rows[0][0]);
        }
    } catch (const std::exception& e) {
        std::cerr << "Error finding pair conversation: " << e.what() << std::endl;
    }
    return 0;
}

int ConversationService::createPairConversation(const User& user, const std::string& otherRole, int otherId) {
    try {
        Database db;
        std::string createdByStudent = "NULL";
        std::string createdByTeacher = "NULL";
        std::string toStudent = "NULL";
        std::string toTeacher = "NULL";

        if (user.role == "teacher") {
            createdByTeacher = std::to_string(user.id);
        } else if (user.role == "student") {
            createdByStudent = std::to_string(user.id);
        } else {
            return 0;
        }

        if (otherRole == "teacher") {
            toTeacher = std::to_string(otherId);
        } else if (otherRole == "student") {
            toStudent = std::to_string(otherId);
        } else {
            return 0;
        }

        db.execute(
            "INSERT INTO conversation (createdby_student_id, createdby_teacher_id, totexting_student_id, totexting_teacher_id, type_conv, is_active) "
            "VALUES (" + createdByStudent + ", " + createdByTeacher + ", " + toStudent + ", " + toTeacher + ", 'pair', 1)"
        );
        auto idRow = db.query("SELECT LAST_INSERT_ID()");
        int conversationId = (!idRow.empty() && !idRow[0].empty()) ? std::stoi(idRow[0][0]) : 0;
        if (conversationId == 0) return 0;

        std::string teacher1 = "NULL";
        std::string student1 = "NULL";
        if (user.role == "teacher") teacher1 = std::to_string(user.id);
        if (user.role == "student") student1 = std::to_string(user.id);

        std::string teacher2 = "NULL";
        std::string student2 = "NULL";
        if (otherRole == "teacher") teacher2 = std::to_string(otherId);
        if (otherRole == "student") student2 = std::to_string(otherId);

        db.execute(
            "INSERT INTO conversation_participant (conversation_id, teacher_id, student_id) "
            "VALUES (" + std::to_string(conversationId) + ", " + teacher1 + ", " + student1 + ")"
        );
        db.execute(
            "INSERT INTO conversation_participant (conversation_id, teacher_id, student_id) "
            "VALUES (" + std::to_string(conversationId) + ", " + teacher2 + ", " + student2 + ")"
        );

        return conversationId;
    } catch (const std::exception& e) {
        std::cerr << "Error creating pair conversation: " << e.what() << std::endl;
        return 0;
    }
}

std::vector<ConversationDTO> ConversationService::getConversationsForUser(const User& user) {
    std::vector<ConversationDTO> conversations;
    try {
        Database db;
        std::string where;
        if (user.role == "teacher") {
            where = "cp.teacher_id = " + std::to_string(user.id);
        } else if (user.role == "student") {
            where = "cp.student_id = " + std::to_string(user.id);
        } else {
            return conversations;
        }

        std::string query = R"(
            SELECT c.id,
                   c.type_conv,
                   COALESCE(c.conversation_name, '') as conversation_name,
                   COALESCE(m.content, '') as last_message,
                   COALESCE(m.message_type, '') as last_message_type,
                   DATE_FORMAT(c.updated_at, '%Y-%m-%dT%H:%i:%sZ') as updated_at
            FROM conversation_participant cp
            JOIN conversation c ON c.id = cp.conversation_id
            LEFT JOIN message m ON m.id = c.last_message_id
            WHERE )" + where + R"(
              AND c.is_active = 1
            ORDER BY c.updated_at DESC
        )";

        auto rows = db.query(query);
        for (const auto& row : rows) {
            ConversationDTO conv;
            conv.id = std::stoi(row[0]);
            conv.type = row[1];
            conv.lastMessage = row[3];
            conv.lastMessageType = row[4];
            conv.updatedAt = row[5];

            if (conv.type == "group") {
                conv.displayName = row[2].empty() ? "Group" : row[2];
            } else {
                auto other = db.query(
                    "SELECT cp.teacher_id, cp.student_id, "
                    "t.first_name, t.last_name, s.first_name, s.last_name "
                    "FROM conversation_participant cp "
                    "LEFT JOIN teacher t ON t.id = cp.teacher_id "
                    "LEFT JOIN student s ON s.id = cp.student_id "
                    "WHERE cp.conversation_id = " + std::to_string(conv.id) + " AND NOT (" + where.substr(3) + ") "
                    "LIMIT 1"
                );
                if (!other.empty()) {
                    std::string tFirst = other[0][2];
                    std::string tLast = other[0][3];
                    std::string sFirst = other[0][4];
                    std::string sLast = other[0][5];
                    if (!tFirst.empty() || !tLast.empty()) {
                        conv.displayName = tFirst + " " + tLast;
                    } else {
                        conv.displayName = sFirst + " " + sLast;
                    }
                } else {
                    conv.displayName = "Conversation";
                }
            }

            conversations.push_back(conv);
        }
    } catch (const std::exception& e) {
        std::cerr << "Error getting conversations: " << e.what() << std::endl;
    }
    return conversations;
}

std::vector<MessageDTO> ConversationService::getMessages(const User& user, int conversationId, int limit, int beforeId) {
    std::vector<MessageDTO> messages;
    try {
        if (!is_member(user, conversationId)) {
            return messages;
        }

        Database db;
        std::string beforeClause = beforeId > 0 ? (" AND m.id < " + std::to_string(beforeId) + " ") : " ";

        std::string query =
            "SELECT m.id, m.content, m.message_type, m.mime_type, m.file_name, "
            "m.sender_teacher_id, t.first_name, t.last_name, "
            "m.sender_student_id, s.first_name, s.last_name "
            "FROM message m "
            "LEFT JOIN teacher t ON t.id = m.sender_teacher_id "
            "LEFT JOIN student s ON s.id = m.sender_student_id "
            "WHERE m.conversation_id = " + std::to_string(conversationId) + beforeClause +
            "ORDER BY m.id DESC LIMIT " + std::to_string(limit);

        auto rows = db.query(query);
        for (const auto& row : rows) {
            MessageDTO msg;
            msg.id = std::stoi(row[0]);
            msg.content = row[1];
            msg.type = row[2];
            msg.mimeType = row[3];
            msg.fileName = row[4];
            msg.conversationId = conversationId;
            msg.createdAt = "";

            if (!row[5].empty()) {
                msg.senderRole = "teacher";
                msg.senderId = std::stoi(row[5]);
                msg.senderName = row[6] + " " + row[7];
            } else {
                msg.senderRole = "student";
                msg.senderId = row[8].empty() ? 0 : std::stoi(row[8]);
                msg.senderName = row[9] + " " + row[10];
            }

            messages.push_back(msg);
        }
    } catch (const std::exception& e) {
        std::cerr << "Error getting messages: " << e.what() << std::endl;
    }
    std::reverse(messages.begin(), messages.end());
    return messages;
}

MessageDTO ConversationService::saveTextMessage(const User& user, int conversationId, const std::string& content) {
    MessageDTO msg;
    try {
        if (!is_member(user, conversationId)) {
            return msg;
        }

        Database db;
        std::string safeContent = db.escape(content);

        std::string senderTeacher = "NULL";
        std::string senderStudent = "NULL";
        if (user.role == "teacher") {
            senderTeacher = std::to_string(user.id);
        } else if (user.role == "student") {
            senderStudent = std::to_string(user.id);
        }

        db.execute(
            "INSERT INTO message (content, message_type, message_status, conversation_id, sender_teacher_id, sender_student_id) "
            "VALUES ('" + safeContent + "', 'text', 'normal', " + std::to_string(conversationId) + ", " + senderTeacher + ", " + senderStudent + ")"
        );

        auto idRow = db.query("SELECT LAST_INSERT_ID()");
        int messageId = (!idRow.empty() && !idRow[0].empty()) ? std::stoi(idRow[0][0]) : 0;

        db.execute(
            "UPDATE conversation SET last_message_id = " + std::to_string(messageId) +
            " WHERE id = " + std::to_string(conversationId)
        );

        msg.id = messageId;
        msg.content = content;
        msg.type = "text";
        msg.mimeType = "";
        msg.fileName = "";
        msg.senderId = user.id;
        msg.senderName = user.fullName();
        msg.senderRole = user.role;
        msg.conversationId = conversationId;
        msg.createdAt = now_iso8601();
    } catch (const std::exception& e) {
        std::cerr << "Error saving message: " << e.what() << std::endl;
    }
    return msg;
}

std::vector<std::string> ConversationService::getConversationMemberSessionKeys(int conversationId) {
    std::vector<std::string> keys;
    try {
        Database db;
        auto rows = db.query(
            "SELECT teacher_id, student_id FROM conversation_participant WHERE conversation_id = " + std::to_string(conversationId)
        );
        for (const auto& row : rows) {
            if (!row[0].empty()) {
                keys.push_back("teacher:" + row[0]);
            } else if (!row[1].empty()) {
                keys.push_back("student:" + row[1]);
            }
        }
    } catch (const std::exception& e) {
        std::cerr << "Error getting conversation members: " << e.what() << std::endl;
    }
    return keys;
}

std::vector<ConversationService::UserSuggestion> ConversationService::getAllUsersWithConversationHint(const User& user) {
    std::vector<UserSuggestion> out;
    try {
        Database db;
        std::string selfCp1 = participantWhereAlias(user.role, user.id, "cp1");
        std::string excludeSelfCp2 = "1=1";
        if (user.role == "teacher") {
            excludeSelfCp2 = "(cp2.teacher_id IS NULL OR cp2.teacher_id <> " + std::to_string(user.id) + ")";
        } else if (user.role == "student") {
            excludeSelfCp2 = "(cp2.student_id IS NULL OR cp2.student_id <> " + std::to_string(user.id) + ")";
        }

        std::string queryPairs =
            "SELECT c.id, "
            "cp2.teacher_id, cp2.student_id, "
            "t.first_name, t.last_name, t.email, "
            "s.first_name, s.last_name, s.email "
            "FROM conversation c "
            "JOIN conversation_participant cp1 ON cp1.conversation_id = c.id "
            "JOIN conversation_participant cp2 ON cp2.conversation_id = c.id "
            "LEFT JOIN teacher t ON t.id = cp2.teacher_id "
            "LEFT JOIN student s ON s.id = cp2.student_id "
            "WHERE c.type_conv = 'pair' "
            "AND " + selfCp1 + " "
            "AND " + excludeSelfCp2 + " ";

        auto pairs = db.query(queryPairs);
        std::map<std::string, int> existing;
        for (const auto& row : pairs) {
            int conversationId = row[0].empty() ? 0 : std::stoi(row[0]);
            if (!row[1].empty()) {
                existing["teacher:" + row[1]] = conversationId;
            } else if (!row[2].empty()) {
                existing["student:" + row[2]] = conversationId;
            }
        }

        std::string teachersQuery = "SELECT id, first_name, last_name, email FROM teacher";
        auto teachers = db.query(teachersQuery);
        for (const auto& t : teachers) {
            int id = std::stoi(t[0]);
            if (user.role == "teacher" && id == user.id) continue;
            UserSuggestion u;
            u.id = id;
            u.role = "teacher";
            u.firstName = t[1];
            u.lastName = t[2];
            u.email = t[3];
            auto it = existing.find("teacher:" + std::to_string(id));
            u.conversationId = it == existing.end() ? 0 : it->second;
            out.push_back(u);
        }

        std::string studentsQuery = "SELECT id, first_name, last_name, email FROM student";
        auto students = db.query(studentsQuery);
        for (const auto& s : students) {
            int id = std::stoi(s[0]);
            if (user.role == "student" && id == user.id) continue;
            UserSuggestion u;
            u.id = id;
            u.role = "student";
            u.firstName = s[1];
            u.lastName = s[2];
            u.email = s[3];
            auto it = existing.find("student:" + std::to_string(id));
            u.conversationId = it == existing.end() ? 0 : it->second;
            out.push_back(u);
        }
    } catch (const std::exception& e) {
        std::cerr << "Error listing users: " << e.what() << std::endl;
    }
    return out;
}
