FROM openjdk:8-jdk-alpine
VOLUME /tmp

ENV USER_NAME pdf4oereb
ENV GROUP_NAME appgroup
ENV APP_HOME /home/$USER_NAME/app

RUN addgroup -S $GROUP_NAME && adduser -S $USER_NAME -G $GROUP_NAME
RUN apk --update add fontconfig ttf-dejavu

RUN mkdir $APP_HOME

ADD build/libs/web-service*.jar $APP_HOME/app.jar
RUN chown $USER_NAME $APP_HOME/app.jar

USER $USER_NAME
WORKDIR $APP_HOME

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]