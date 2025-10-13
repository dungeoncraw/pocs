# ---- Build stage ----
FROM rust:1.80 as builder

WORKDIR /app

# Cache deps
COPY Cargo.toml ./
RUN mkdir src && echo "fn main() {}" > src/main.rs
RUN cargo build --release || true

# Copy actual source
COPY src ./src
COPY migrations ./migrations

# Build release
RUN cargo build --release

# ---- Runtime stage ----
FROM debian:bookworm-slim

# Add a non-root user
RUN useradd -m appuser

WORKDIR /app
COPY --from=builder /app/target/release/bitonic_service /usr/local/bin/bitonic_service
COPY migrations ./migrations

ENV RUST_LOG=info
EXPOSE 8080

USER appuser
CMD ["/usr/local/bin/bitonic_service"]