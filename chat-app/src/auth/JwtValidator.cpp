#include "JwtValidator.hpp"
#include "jwt-cpp/jwt.h"
#include "../db/Database.hpp"
#include <cstdlib>

const std::string& JwtValidator::secret() {
    static const std::string s = [] {
        const char* env = std::getenv("JWT_SECRET");
        return env ? std::string(env) : std::string("your-super-secret-key");
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
    try {
        user.id = std::stoi(decoded.get_subject());
    } catch (...) {
        user.id = static_cast<int>(decoded.get_payload_claim("sub").as_number());
    }

    if (decoded.has_payload_claim("email")) {
        user.email = decoded.get_payload_claim("email").as_string();
    }
    if (decoded.has_payload_claim("role")) {
        user.role = decoded.get_payload_claim("role").as_string();
    }
    if (decoded.has_payload_claim("first_name")) {
        user.firstName = decoded.get_payload_claim("first_name").as_string();
    }
    if (decoded.has_payload_claim("last_name")) {
        user.lastName = decoded.get_payload_claim("last_name").as_string();
    }

    if (user.role.empty() || user.firstName.empty() || user.lastName.empty() || user.email.empty()) {
        Database db;

        auto teacherRows = db.query(
            "SELECT first_name, last_name, email FROM teacher WHERE id = " + std::to_string(user.id) + " LIMIT 1"
        );
        if (!teacherRows.empty()) {
            user.role = "teacher";
            user.firstName = teacherRows[0][0];
            user.lastName = teacherRows[0][1];
            user.email = teacherRows[0][2];
        } else {
            auto studentRows = db.query(
                "SELECT first_name, last_name, email FROM student WHERE id = " + std::to_string(user.id) + " LIMIT 1"
            );
            if (!studentRows.empty()) {
                user.role = "student";
                user.firstName = studentRows[0][0];
                user.lastName = studentRows[0][1];
                user.email = studentRows[0][2];
            } else {
                user.role = user.role.empty() ? "teacher" : user.role;
                user.firstName = user.firstName.empty() ? "User" : user.firstName;
                user.lastName = user.lastName.empty() ? std::to_string(user.id) : user.lastName;
            }
        }
    }
    return user;
}
