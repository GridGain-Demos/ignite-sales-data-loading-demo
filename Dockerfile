# Start from GridGain Professional image.
FROM gridgain/ultimate:8.7.20

# Fix Permission Denied errors below
USER root

# Set config uri for node.
ENV CONFIG_URI sales-server.xml

# Copy optional libs.
ENV OPTION_LIBS ignite-rest-http

### Custom Variables for this image
# Set IGNITE REST JETTY PORT
ENV IGNITE_JETTY_PORT 8031
# Set Ignite to be verbose
ENV IGNITE_QUIET false

# Update packages and install maven.
RUN set -x \
    && apk add --no-cache \
        openjdk8

RUN apk --update add \
    maven \
    && rm -rfv /var/cache/apk/*

# Append project to container.
ADD . sales

# Build project in container.
RUN mvn -f salesdataloaders/pom.xml clean package -DskipTests

# Copy project jars to node classpath.
RUN mkdir $IGNITE_HOME/libs/sales && \
   find salesdataloaders/target -name "*.jar" -type f -exec cp {} $IGNITE_HOME/libs/sales \;

# gridgain/ultimate does not include the REST Port which is needed for monitoring by the Web Console Agent
# Add REST and any other custom port assignments
EXPOSE 8031 31500 31400 31100 11231 10831