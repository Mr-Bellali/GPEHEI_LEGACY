#include "Session.hpp"
#include "SessionManager.hpp"
#include "MessageHandler.hpp"
#include "../auth/JwtValidator.hpp"
#include <iostream>

Session::Session(tcp::socket socket)
    : ws_(std::move(socket)) {}

void Session::start() {
    // Set up the decorator for the WebSocket response
    ws_.set_option(
        websocket::stream_base::decorator(
            [](websocket::response_type& res) {
                res.set(boost::beast::http::field::server, "GPEHEI");
            }));
    
    // Async accept — handshake happens here
    ws_.async_accept(
        [self = shared_from_this()](beast::error_code ec) {
            if (!ec) {
                // After successful handshake, wait for authentication token
                self->do_read();
            }
        });
}

void Session::do_read() {
    ws_.async_read(buffer_,
        [self = shared_from_this()](beast::error_code ec, std::size_t) {
            if (ec) {
                std::cout << "Client disconnected: " << ec.message() << std::endl;
                if (self->authenticated_) {
                    SessionManager::instance().removeSession(self->user_.sessionKey());
                }
                return;
            }

            std::string msg = beast::buffers_to_string(self->buffer_.data());
            std::cout << "Received: " << msg << std::endl;

            try {
                if (!self->authenticated_) {
                    // First message must be JWT token (plain string)
                    self->user_ = JwtValidator::validate(msg);
                    self->authenticated_ = true;
                    SessionManager::instance().addSession(self->user_.sessionKey(), self);
                    std::cout << "User authenticated: " << self->user_.fullName() << " (" << self->user_.role << ")" << std::endl;
                    self->send(std::string("{\"event\":\"auth\",\"data\":{\"id\":") + std::to_string(self->user_.id) +
                               ",\"role\":\"" + self->user_.role + "\",\"first_name\":\"" + self->user_.firstName +
                               "\",\"last_name\":\"" + self->user_.lastName + "\",\"email\":\"" + self->user_.email + "\"}}");
                } else {
                    // Handle actual JSON messages
                    MessageHandler::handle(msg, self->user_, self);
                }
            } catch (const std::exception& e) {
                std::cerr << "Error: " << e.what() << std::endl;
                if (!self->authenticated_) {
                    // Failed to authenticate; close connection
                    self->ws_.async_close(websocket::close_code::normal,
                        [self](beast::error_code) {});
                    return;
                }
            }

            self->buffer_.consume(self->buffer_.size());
            self->do_read(); // loop: wait for the next message
        });
}

void Session::send(std::string message) {
    net::post(ws_.get_executor(),
        [self = shared_from_this(), msg = std::move(message)]() mutable {
            self->writeQueue_.push_back(std::move(msg));
            if (!self->writing_) {
                self->do_write();
            }
        });
}

void Session::do_write() {
    if (writeQueue_.empty()) {
        writing_ = false;
        return;
    }

    writing_ = true;
    ws_.text(true);
    ws_.async_write(net::buffer(writeQueue_.front()),
        [self = shared_from_this()](beast::error_code ec, std::size_t) {
            if (ec) {
                std::cout << "Write error: " << ec.message() << std::endl;
                return;
            }

            self->writeQueue_.pop_front();
            self->do_write();
        });
}
