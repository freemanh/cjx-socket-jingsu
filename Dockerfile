FROM maven:3.3.9-jdk-8
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN ["mvn", "clean", "install"]
CMD ["java", "-jar","cjx-socket-jingsu.jar"]
