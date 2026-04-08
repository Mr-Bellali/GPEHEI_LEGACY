#include <boost/beast/core.hpp>
#include <boost/beast/websocket.hpp>
#include <boost/asio/ip/tcp.hpp>
#include <iostream>

namespace beast = boost::beast;
namespace websocket = beast::websocket;
namespace net = boost::asio;
using tcp = net::ip::tcp;

// Handle ONE client
void handle_client(tcp::socket socket) {
    try {
        websocket::stream<tcp::socket> ws(std::move(socket));

        // Accept WebSocket handshake
        ws.accept();

        std::cout << "Client connected!" << std::endl;

        while (true) {
            beast::flat_buffer buffer;

            // Read message
            ws.read(buffer);

            std::string msg = beast::buffers_to_string(buffer.data());
            std::cout << "Received: " << msg << std::endl;

            // Echo back
            ws.text(ws.got_text());
            ws.write(buffer.data());
        }
    }
    catch (std::exception const& e) {
        std::cout << "Client disconnected: " << e.what() << std::endl;
    }
}

int main() {
    try {
        net::io_context ioc;

        // Listen on port 8080
        tcp::acceptor acceptor(ioc, {tcp::v4(), 8080});

        std::cout << "WebSocket server running on port 8080..." << std::endl;

        while (true) {
            tcp::socket socket(ioc);

            // Wait for client
            acceptor.accept(socket);

            // Handle client (simple version = blocking)
            handle_client(std::move(socket));
        }
    }
    catch (std::exception const& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }
}