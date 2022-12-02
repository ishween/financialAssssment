FROM  openjdk:11

ADD target/*.war financialAssessment.war

EXPOSE 8080
RUN date

#Sample JAVA_OPTS=-Xms256m -Xmx1600m -XX:+CrashOnOutOfMemoryError
ENV JAVA_OPTS=
#CMD java $JAVA_OPTS -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager -jar irs.war
CMD java $JAVA_OPTS -jar target/dependency/webapp-runner.jar --port $PORT target/*.war


