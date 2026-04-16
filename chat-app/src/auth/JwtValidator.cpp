#include "JwtValidator.hpp"
#include "jwt-cpp/jwt.h"
#include <cstdlib>

const std::string& JwtValidator::secret() {
    static const std::string s = [] {
        const char* env = std::getenv("JWT_SECRET");
        return env ? std::string(env) : std::string("change-me-in-production");
    }();
    return s;
}

std::string JwtValidator::extractToken(const std::string& target) {
    auto pos = target.find("token=");
    if (pos == std::string::npos) return "";
    pos += 6;
    auto end = target.find('&', pos);
    return target.substr(pos, end == std::string::npos ? std::string::npos : end - pos);
}

User JwtValidator::validate(const std::string& token) {
    if (token.empty()) throw std::runtime_error("Missing token");
    auto decoded = jwt::decode(token);
    jwt::verify()
        .allow_algorithm(jwt::algorithm::hs256{secret()})
        .verify(decoded);
    User user;
    user.id        = std::stoi(decoded.get_subject());
    user.role      = decoded.get_payload_claim("role").as_string();
    user.email     = decoded.get_payload_claim("email").as_string();
    user.firstName = decoded.get_payload_claim("first_name").as_string();
    user.lastName  = decoded.get_payload_claim("last_name").as_string();
    return user;
}
