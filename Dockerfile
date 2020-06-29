FROM openjdk:11
VOLUME /tmp
COPY target/quotes-aggregator-1.0.0.jar /opt/quotes-aggregator.jar
ENTRYPOINT ["java",  "-XX:+UseParallelGC", "-Xms2048m", "-Xmx4096m", "-XX:MetaspaceSize=2048m", "-XX:+UnlockExperimentalVMOptions",  "-Djava.net.preferIPv4Stack=true", "-jar","/opt/quotes-aggregator.jar"]
EXPOSE 6050