#include "server/Server.hpp"
#include <iostream>

int main() {
    try {
        net::io_context ioc;
        Server server(ioc, 8080);
        std::cout << "WebSocket server running on port 8080 (async)...\n";
        ioc.run(); // single thread drives all clients via the event loop
    } catch (std::exception const& e) {
        std::cerr << "Error: " << e.what() << "\n";
    }
}
