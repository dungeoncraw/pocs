#!/bin/bash

# Docker operations helper script for Dungeon Game

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

IMAGE_NAME="dungeon-game"
CONTAINER_NAME="dungeon-game-app"

function print_usage() {
    echo -e "${YELLOW}Usage: $0 {build|run|test|stop|clean|logs}${NC}"
    echo ""
    echo "Commands:"
    echo "  build  - Build Docker image"
    echo "  run    - Run the application in Docker container"
    echo "  test   - Run tests in Docker container"
    echo "  stop   - Stop running container"
    echo "  clean  - Remove container and image"
    echo "  logs   - Show container logs"
}

function build_image() {
    echo -e "${GREEN}Building Docker image...${NC}"
    docker build -t $IMAGE_NAME .
    echo -e "${GREEN}Docker image built successfully!${NC}"
}

function run_container() {
    echo -e "${GREEN}Running Docker container...${NC}"
    docker run -d \
        --name $CONTAINER_NAME \
        -p 8080:8080 \
        $IMAGE_NAME
    echo -e "${GREEN}Container started successfully!${NC}"
    echo "Access the application at: http://localhost:8080"
}

function run_tests() {
    echo -e "${GREEN}Running tests in Docker container...${NC}"
    docker run --rm \
        -v $(pwd):/app \
        -w /app \
        maven:3.9.4-eclipse-temurin-21 \
        mvn test
    echo -e "${GREEN}Tests completed!${NC}"
}

function stop_container() {
    echo -e "${YELLOW}Stopping container...${NC}"
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker rm $CONTAINER_NAME 2>/dev/null || true
    echo -e "${GREEN}Container stopped and removed!${NC}"
}

function clean_docker() {
    echo -e "${YELLOW}Cleaning up Docker resources...${NC}"
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker rm $CONTAINER_NAME 2>/dev/null || true
    docker rmi $IMAGE_NAME 2>/dev/null || true
    echo -e "${GREEN}Cleanup completed!${NC}"
}

function show_logs() {
    echo -e "${GREEN}Showing container logs...${NC}"
    docker logs -f $CONTAINER_NAME
}

# Main execution
case "$1" in
    build)
        build_image
        ;;
    run)
        stop_container
        build_image
        run_container
        ;;
    test)
        run_tests
        ;;
    stop)
        stop_container
        ;;
    clean)
        clean_docker
        ;;
    logs)
        show_logs
        ;;
    *)
        print_usage
        exit 1
        ;;
esac
