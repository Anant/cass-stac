# Use a Debian-based Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set environment variables
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=$JAVA_HOME/bin:$PATH
ENV SDKMAN_DIR="/root/.sdkman"
ENV PATH="$SDKMAN_DIR/bin:$PATH"
ENV TERM=xterm

# Install necessary packages including build tools and jq
RUN apt-get update && apt-get install -y \
    curl \
    tar \
    git \
    python3 \
    maven \
    wget \
    jq \
    && apt-get clean

# Manually download and set up Astra CLI (replace with correct URL)
RUN curl -Ls "https://downloads.datastax.com/enterprise/astra-cli/latest/astra-cli-linux-amd64" -o /usr/local/bin/astra && \
    chmod +x /usr/local/bin/astra

# Download and extract cqlsh-astra
RUN curl -O https://downloads.datastax.com/enterprise/cqlsh-astra-20221114-bin.tar.gz && \
    tar xvfz cqlsh-astra-20221114-bin.tar.gz && \
    mv cqlsh-astra /usr/local/bin

# Invalidate cache for Git repository download
ADD https://api.github.com/repos/Anant/cass-stac/git/refs/heads/main version.json
RUN rm -rf /app && mkdir -p /app && git clone -b main https://github.com/Anant/cass-stac.git /app

# Create and set the working directory
WORKDIR /app

# Make dockersetup.sh script executable
RUN chmod +x /app/dockersetup.sh /app/setup_devcontainer.sh

# Set environment variables using build arguments
ARG ASTRA_DB_USERNAME
ARG ASTRA_DB_KEYSPACE
ARG ASTRA_DB_ID
ARG DATASTAX_ASTRA_PASSWORD
ARG DATASTAX_ASTRA_SCB_NAME=secure-connect-database.zip
ENV ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME
ENV ASTRA_DB_KEYSPACE=$ASTRA_DB_KEYSPACE
ENV ASTRA_DB_ID=$ASTRA_DB_ID
ENV DATASTAX_ASTRA_PASSWORD=$DATASTAX_ASTRA_PASSWORD
ENV DATASTAX_ASTRA_SCB_NAME=$DATASTAX_ASTRA_SCB_NAME
