FROM rust:1.84-slim-bullseye as builder

ENV SERVER_HOST=${server_host}
ENV SERVER_PORT=${server_port}

WORKDIR /webapi

COPY . .

RUN cargo build --release

FROM gcr.io/distroless/cc

WORKDIR /webapi

COPY --from=builder /webapi/target/release/web_api .

EXPOSE 80

CMD ["./web_api"]