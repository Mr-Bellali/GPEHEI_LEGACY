#include "SessionManager.hpp"
#include "Session.hpp"

SessionManager& SessionManager::instance() {
    static SessionManager mgr;
    return mgr;
}

void SessionManager::addSession(const std::string& key, std::shared_ptr<Session> session) {
    sessions_[key] = session;
}

void SessionManager::removeSession(const std::string& key) {
    sessions_.erase(key);
}

std::shared_ptr<Session> SessionManager::getSession(const std::string& key) {
    auto it = sessions_.find(key);
    return it != sessions_.end() ? it->second : nullptr;
}

void SessionManager::broadcast(const std::string& message) {
    for (auto& [key, session] : sessions_) {
        if (session) {
            session->send(message);
        }
    }
}

void SessionManager::broadcastToRole(const std::string& message, const std::string& role) {
    for (auto& [key, session] : sessions_) {
        if (session && key.substr(0, key.find(':')) == role) {
            session->send(message);
        }
    }
}

void SessionManager::sendTo(const std::string& key, const std::string& message) {
    auto session = getSession(key);
    if (session) {
        session->send(message);
    }
}
