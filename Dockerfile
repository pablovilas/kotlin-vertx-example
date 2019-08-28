FROM azul/zulu-openjdk-alpine:11.0.4-jre

ENV VERTICLE_FILE kotlin-vertx-example-1.0.0-SNAPSHOT-all.jar
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

COPY build/libs/$VERTICLE_FILE $VERTICLE_HOME/

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]
