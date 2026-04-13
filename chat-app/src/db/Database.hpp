#pragma once

#include <mysql/mysql.h>
#include <string>
#include <vector>
#include <stdexcept>

// Row = ordered list of column values (NULL comes back as empty string)
using Row = std::vector<std::string>;
using ResultSet = std::vector<Row>;

class Database
{
public:
    // Reads DB_HOST / DB_USER / DB_PASSWORD / DB_NAME / DB_PORT from env
    Database();
    ~Database();

    // Non-copyable — each instance owns one connection
    Database(const Database &) = delete;
    Database &operator=(const Database &) = delete;

    // Run INSERT / UPDATE / DELETE — throws on error
    void execute(const std::string &query);

    // Run SELECT — returns all rows
    ResultSet query(const std::string &sql);

    // Escape a string to prevent SQL injection when building queries manually
    std::string escape(const std::string &value);

private:
    MYSQL *conn_;
};
