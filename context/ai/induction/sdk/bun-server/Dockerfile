FROM oven/bun:1.1 AS base
WORKDIR /app

# Copy package.json and bun.lockb (if exists)
COPY package.json ./
# COPY bun.lockb ./

# Install dependencies
RUN bun install

# Copy source code
COPY . .

# Expose port
EXPOSE 4000

# Run the server
CMD ["bun", "run", "index.ts"]
