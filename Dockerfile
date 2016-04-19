FROM maven:3.3.9-jdk-8
ADD . /tmp/build/
RUN cd /tmp/build && mvn clean package && mv target/*.jar /app.jar \
    && cd / && rm -rf /tmp/build
EXPOSE 8001
CMD ["java", "-jar","app.jar"]
