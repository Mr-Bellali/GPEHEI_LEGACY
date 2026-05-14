#pragma once
#include "../models/User.hpp"
#include <string>
#include <stdexcept>

class JwtValidator {
public:
    // Extract "token" query param from a WS upgrade target like "/?token=xxx&y=z"
    static std::string extractToken(const std::string& target);
    // Verify JWT signature and claims; returns a populated User or throws std::runtime_error
    static User validate(const std::string& token);
private:
    // Lazily reads JWT_SECRET from env (falls back to dev default on first call)
    static const std::string& secret();
};
