FROM gcc:14-bookworm

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libboost-all-dev \
        libssl-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY main.cpp ./

RUN g++ main.cpp \
    -O2 \
    -std=c++17 \
    -pthread \
    -lboost_system \
    -lssl \
    -lcrypto \
    -o chat-server

EXPOSE 8080

CMD ["./chat-server"]