#pragma once

#include <boost/asio/ip/tcp.hpp>
#include <boost/beast/core.hpp>
#include <memory>

namespace net = boost::asio;
using tcp = net::ip::tcp;

class Server {
public:
    explicit Server(net::io_context& ioc, unsigned short port);

private:
    void do_accept();

    tcp::acceptor acceptor_;
};
