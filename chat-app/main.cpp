#include <boost/beast/core.hpp>
#include <boost/beast/websocket.hpp>
#include <boost/asio/ip/tcp.hpp>
#include <iostream>
#include <memory>

namespace beast = boost::beast;
namespace websocket = beast::websocket;
namespace net = boost::asio;
using tcp = net::ip::tcp;

// Session owns the state of one connected client.
// shared_ptr keeps it alive for as long as any async operation references it —
// when the last callback finishes, the ref-count drops to zero and it's destroyed.
class Session : public std::enable_shared_from_this<Session> {
public:
    explicit Session(tcp::socket socket)
        : ws_(std::move(socket)) {}

    void start() {
        // Async handshake — returns immediately, callback fires when done
        ws_.async_accept(
            [self = shared_from_this()](beast::error_code ec) {
                if (!ec) self->do_read();
            });
    }

private:
    void do_read() {
        // Async read — returns immediately, callback fires when a full message arrives
        ws_.async_read(buffer_,
            [self = shared_from_this()](beast::error_code ec, std::size_t) {
                if (ec) {
                    std::cout << "Client disconnected: " << ec.message() << std::endl;
                    return;
                }
                std::string msg = beast::buffers_to_string(self->buffer_.data());
                std::cout << "Received: " << msg << std::endl;
                self->do_write();
            });
    }

    void do_write() {
        ws_.text(ws_.got_text());
        // Async write — returns immediately, callback fires when the write completes
        ws_.async_write(buffer_.data(),
            [self = shared_from_this()](beast::error_code ec, std::size_t) {
                self->buffer_.consume(self->buffer_.size());
                if (ec) {
                    std::cout << "Write error: " << ec.message() << std::endl;
                    return;
                }
                self->do_read(); // loop: wait for the next message
            });
    }

    websocket::stream<tcp::socket> ws_;
    beast::flat_buffer buffer_;
};

// Accepts connections in a loop without blocking.
// Each accepted socket is handed off to a new Session immediately,
// then async_accept is posted again — the thread never waits on a client.
class Server {
public:
    Server(net::io_context& ioc, unsigned short port)
        : acceptor_(ioc, {tcp::v4(), port}) {
        do_accept();
    }

private:
    void do_accept() {
        acceptor_.async_accept(
            [this](beast::error_code ec, tcp::socket socket) {
                if (!ec) {
                    auto ep = socket.remote_endpoint();
                    std::cout << "Client connected: "
                              << ep.address().to_string() << ":" << ep.port() << std::endl;
                    std::make_shared<Session>(std::move(socket))->start();
                }
                do_accept(); // immediately wait for the next client
            });
    }

    tcp::acceptor acceptor_;
};

int main() {
    try {
        net::io_context ioc;
        Server server(ioc, 8080);
        std::cout << "WebSocket server running on port 8080 (async)..." << std::endl;
        ioc.run(); // single thread drives all clients via the event loop
    }
    catch (std::exception const& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }
}