#pragma once

#include <map>
#include <memory>
#include <string>

class Session;

class SessionManager {
public:
    static SessionManager& instance();

    // Add a session to the registry
    void addSession(const std::string& key, std::shared_ptr<Session> session);

    // Remove a session from the registry
    void removeSession(const std::string& key);

    // Get a session by key
    std::shared_ptr<Session> getSession(const std::string& key);

    // Broadcast a message to all sessions
    void broadcast(const std::string& message);

    // Broadcast to specific role(s)
    void broadcastToRole(const std::string& message, const std::string& role);

    void sendTo(const std::string& key, const std::string& message);

private:
    SessionManager() = default;

    std::map<std::string, std::shared_ptr<Session>> sessions_;
};
