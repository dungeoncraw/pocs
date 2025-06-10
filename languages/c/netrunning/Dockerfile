FROM ubuntu:latest AS builder

RUN apt-get update && \
    apt-get install -y \
    build-essential \
    cmake

WORKDIR /app

COPY . .

RUN cmake -Bbuild -H. && cmake --build build

FROM ubuntu:latest

# RUN apt-get update && apt-get install -y libxyz

WORKDIR /app

COPY --from=builder /app/build/netrunner .

# Define the command to run your application
CMD ["./netrunner"]