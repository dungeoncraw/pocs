FROM rust:1.85-slim AS builder

WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y \
    build-essential \
    pkg-config \
    python3 \
    libsasl2-dev \
    libssl-dev \
    zlib1g-dev \
    && rm -rf /var/lib/apt/lists/*
RUN cargo build --release

FROM debian:bookworm-slim
RUN apt-get update && apt-get install -y libssl-dev ca-certificates && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=builder /app/target/release/memkafka .
CMD ["./memkafka"]
