# Use an official sbt image
FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1 AS build

# Set the working directory
WORKDIR /app

# Copy the project files
COPY . .

# Compile
RUN sbt compile

# Command to run the PoC demo
CMD ["sbt", "run"]
