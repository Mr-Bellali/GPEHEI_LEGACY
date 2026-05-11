#include "Database.hpp"
#include <cstdlib>
#include <stdexcept>
#include <string>

// Helper: read env var or throw if missing
static std::string required_env(const char* name) {
    const char* val = std::getenv(name);
    if (!val) throw std::runtime_error(std::string("Missing environment variable: ") + name);
    return val;
}

static unsigned int optional_port() {
    const char* val = std::getenv("DB_PORT");
    return val ? static_cast<unsigned int>(std::stoul(val)) : 3306u;
}

// Constructor / Destructor
Database::Database() {
    conn_ = mysql_init(nullptr);
    if (!conn_) throw std::runtime_error("mysql_init failed");

    std::string host     = required_env("DB_HOST");
    std::string user     = required_env("DB_USER");
    std::string password = required_env("DB_PASSWORD");
    std::string dbname   = required_env("DB_NAME");
    unsigned int port    = optional_port();

    if (!mysql_real_connect(conn_,
            host.c_str(), user.c_str(), password.c_str(),
            dbname.c_str(), port, nullptr, 0)) {
        std::string err = mysql_error(conn_);
        mysql_close(conn_);
        throw std::runtime_error("MySQL connection failed: " + err);
    }
}

Database::~Database() {
    mysql_close(conn_);
}

// Execute 
void Database::execute(const std::string& sql) {
    if (mysql_query(conn_, sql.c_str())) {
        throw std::runtime_error("MySQL execute failed: " + std::string(mysql_error(conn_)));
    }
}

// Query
ResultSet Database::query(const std::string& sql) {
    if (mysql_query(conn_, sql.c_str())) {
        throw std::runtime_error("MySQL query failed: " + std::string(mysql_error(conn_)));
    }

    MYSQL_RES* result = mysql_store_result(conn_);
    if (!result) {
        throw std::runtime_error("mysql_store_result failed: " + std::string(mysql_error(conn_)));
    }

    ResultSet rows;
    unsigned int num_fields = mysql_num_fields(result);
    MYSQL_ROW row;

    while ((row = mysql_fetch_row(result))) {
        Row r;
        r.reserve(num_fields);
        for (unsigned int i = 0; i < num_fields; ++i) {
            r.emplace_back(row[i] ? row[i] : "");
        }
        rows.push_back(std::move(r));
    }

    mysql_free_result(result);
    return rows;
}

// Escape
std::string Database::escape(const std::string& value) {
    // Worst case: every character is escaped + null terminator
    std::string buf(value.size() * 2 + 1, '\0');
    unsigned long len = mysql_real_escape_string(conn_, buf.data(),
                                                  value.c_str(), value.size());
    buf.resize(len);
    return buf;
}
