# Start from GridGain Professional image.
FROM gridgain/gridgain-pro:8.7.16

# Set config uri for node.
ENV CONFIG_URI sales-server.xml

# Copy optional libs.
ENV OPTION_LIBS ignite-rest-http

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