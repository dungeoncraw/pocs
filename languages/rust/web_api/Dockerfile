FROM rust:1.84-slim-bullseye as builder

WORKDIR /webapi

COPY . .

RUN cargo build --release

FROM gcr.io/distroless/cc

WORKDIR /webapi

COPY --from=builder /webapi/target/release/web_api .