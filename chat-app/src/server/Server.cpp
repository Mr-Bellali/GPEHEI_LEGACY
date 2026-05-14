#include "Server.hpp"
#include "Session.hpp"
#include <iostream>

Server::Server(net::io_context& ioc, unsigned short port)
    : acceptor_(ioc, {tcp::v4(), port}) {
    do_accept();
}

void Server::do_accept() {
    acceptor_.async_accept(
        [this](boost::beast::error_code ec, tcp::socket socket) {
            if (!ec) {
                auto ep = socket.remote_endpoint();
                std::cout << "Client connected: "
                          << ep.address().to_string() << ":" << ep.port() << std::endl;
                std::make_shared<Session>(std::move(socket))->start();
            }
            do_accept(); // immediately wait for the next client
        });
}
