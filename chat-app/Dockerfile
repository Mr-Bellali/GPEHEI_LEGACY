FROM gcc:14-bookworm

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libboost-all-dev \
        libssl-dev \
        libmariadb-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY src/        ./src/
COPY jwt-cpp/    ./jwt-cpp/
COPY picojson/   ./picojson/

RUN g++ \
        src/main.cpp \
        src/server/Server.cpp \
        src/server/Session.cpp \
        src/server/SessionManager.cpp \
        src/auth/JwtValidator.cpp \
        src/db/Database.cpp \
    -Isrc \
    -I. \
    -O2 \
    -std=c++17 \
    -pthread \
    -lboost_system \
    -lssl \
    -lcrypto \
    -lmysqlclient \
    -o chat-server

EXPOSE 8080

CMD ["./chat-server"]