FROM openjdk:16-alpine
ARG JAR_FILE
ENV JAR_FILE=$JAR_FILE

RUN apk update && apk add curl jq
RUN mkdir /opt/demo-app
RUN addgroup -S demo && adduser -S demo -G demo
RUN chown demo:demo /opt/demo-app

USER demo
WORKDIR /opt/demo-app

COPY $JAR_FILE .

EXPOSE 8080
ENTRYPOINT java $JAVA_OPTS -jar $JAR_FILE
HEALTHCHECK --start-period=30s --interval=30s --timeout=5s --retries=3 \
  CMD (curl -sf http://localhost:8080/actuator/health || echo '{}') | jq --exit-status '.status=="UP"' || exit 1
