FROM maven:3.3.9-jdk-8
RUN ["mvn", "clean", "install"]
CMD ["java", "-jar","cjx-socket-jingsu.jar"]
