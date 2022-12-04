FROM openjdk:11 as rabbitmq
EXPOSE 8080
WORKDIR /app

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file
COPY pom.xml .

# Copy the project source
COPY ./src ./src
COPY ./pom.xml ./pom.xml

RUN chmod 755 /app/mvnw

RUN ./mvnw package -DskipTests

ADD target/financialAssessment-0.0.1-SNAPSHOT.jar financialAssessment.jar

#CMD docker-compose up
ENTRYPOINT ["java","-jar","financialAssessment.jar"]


#FROM  openjdk:11
#FROM rabbitmq:3.8.0-management
#
###
#ADD target/financialAssessment-0.0.1-SNAPSHOT.jar financialAssessment.jar
#
#CMD java -jar financialAssessment.jar


#COPY target/financialAssessment-*.jar /financialAssessment.jar
##
##
#EXPOSE 8080
#RUN date
#
#CMD java -jar target/financialAssessment-0.0.1-SNAPSHOT.jar
#CMD ["java", "-jar", "com/example/financialAssessment/financialAssessment.war"]

#Sample JAVA_OPTS=-Xms256m -Xmx1600m -XX:+CrashOnOutOfMemoryError
#ENV JAVA_OPTS=
#CMD java $JAVA_OPTS src/main/java/com/example/financialassessment/FinancialAssessmentApplication.java
#CMD java $JAVA_OPTS -jar target/dependency/webapp-runner.jar --port $PORT target/*.war
#RUN javac FinancialAssessmentApplication.java
#CMD ["java", "FinancialAssessmentApplication"]
#CMD ["java","-jar","*.jar"]

#
#FROM openjdk:11
#COPY target/financialAssessment-*.jar /financialAssessment.jar
#WORKDIR /tmp
#ENTRYPOINT ["java","FinancialAssessmentApplication"]


