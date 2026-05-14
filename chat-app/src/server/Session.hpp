#pragma once

#include <boost/asio/ip/tcp.hpp>
#include <boost/beast/core.hpp>
#include <boost/beast/websocket.hpp>
#include <deque>
#include <memory>
#include "../models/User.hpp"

namespace beast = boost::beast;
namespace websocket = beast::websocket;
namespace net = boost::asio;
using tcp = net::ip::tcp;

class Session : public std::enable_shared_from_this<Session> {
public:
    explicit Session(tcp::socket socket);

    void start();
    void send(std::string message);
    const User& user() const { return user_; }

private:
    void do_read();
    void do_write();

    websocket::stream<tcp::socket> ws_;
    beast::flat_buffer buffer_;
    std::deque<std::string> writeQueue_;
    bool writing_ = false;
    User user_; // authenticated user
    bool authenticated_ = false;
};
