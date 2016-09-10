FROM openjdk:8-jdk-alpine

RUN apk add --no-cache curl tar bash

ARG MAVEN_VERSION=3.3.9

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    | tar -xzC /usr/share/maven --strip-components=1 \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

ADD pom.xml /tmp/build/
RUN cd /tmp/build && mvn dependency:resolve-plugins && mvn -q dependency:resolve
ADD src /tmp/build/src
RUN cd /tmp/build && mvn clean compile && mv target/*.jar /app.jar \
    && cd / && rm -rf /tmp/build
    
EXPOSE 8001
CMD ["java", "-jar","app.jar"]
