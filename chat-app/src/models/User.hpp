#pragma once
#include <string>

struct User {
    int         id        = 0;
    std::string firstName;
    std::string lastName;
    std::string email;
    // "student" | "teacher" | "admin"
    std::string role;
    std::string fullName()   const { return firstName + " " + lastName; }
    // Unique registry key used by SessionManager
    std::string sessionKey() const { return role + ":" + std::to_string(id); }
};
