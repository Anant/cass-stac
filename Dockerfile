# Use a Debian-based Java runtime as a parent image
FROM ghcr.io/graalvm/jdk-community:22

# Set environment variables
ENV JAVA_HOME=/usr/lib64/graalvm/graalvm-community-java22
ENV PATH=$JAVA_HOME/bin:$PATH
ENV SDKMAN_DIR="/root/.sdkman"
ENV PATH="$SDKMAN_DIR/bin:$PATH"
ENV TERM=xterm

# Install necessary packages including build tools and jq
RUN microdnf update && microdnf install -y \
    curl \
    tar \
    git \
    python3 \
    maven \
    wget \
    jq \
    && microdnf clean all

# Manually download and set up Astra CLI (replace with correct URL)
RUN curl -Ls "https://downloads.datastax.com/enterprise/astra-cli/latest/astra-cli-linux-amd64" -o /usr/local/bin/astra && \
    chmod +x /usr/local/bin/astra

# Download and extract cqlsh-astra
RUN curl -O https://downloads.datastax.com/enterprise/cqlsh-astra-20221114-bin.tar.gz && \
    tar xvfz cqlsh-astra-20221114-bin.tar.gz && \
    mv cqlsh-astra /usr/local/bin

# Invalidate cache for Git repository download
ADD https://api.github.com/repos/Anant/cass-stac/git/refs/heads/main version.json
RUN rm -rf /app && mkdir -p /app && cd /app && git clone -b main https://github.com/Anant/cass-stac.git /app

# Create and set the working directory
WORKDIR /app

# Make dockersetup.sh script executable
RUN chmod +x /app/dockersetup.sh

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

# Run the dockersetup.sh script
# RUN /app/dockersetup.sh

# Package the Maven project
RUN mvn package -DskipTests=true

# Run the Spring Boot application with server port overridden
CMD ["sh", "-c", "/app/dockersetup.sh && mvn spring-boot:run -Dspring-boot.run.arguments=\"--server.port=${SERVER_PORT} --datastax.astra.username=${ASTRA_DB_USERNAME} --datastax.astra.password=${DATASTAX_ASTRA_PASSWORD} --datastax.astra.keyspace=${ASTRA_DB_KEYSPACE} --datastax.astra.secure-connect-bundle=${DATASTAX_ASTRA_SCB_NAME} \""]

