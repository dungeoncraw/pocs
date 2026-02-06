# Use the official Rust image as a builder
FROM rust:1.80-slim as builder

WORKDIR /usr/src/app
COPY . .

RUN cargo build --release

# Use a smaller image for the final container
FROM debian:bookworm-slim

RUN apt-get update && apt-get install -y libssl-dev ca-certificates curl && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/local/bin
COPY --from=builder /usr/src/app/target/release/rust-profile-api .

EXPOSE 3000

CMD ["./rust-profile-api"]
