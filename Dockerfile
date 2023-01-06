FROM openjdk:17-jdk-alpine
ADD target/auth-server-0.0.1.jar app.jar
VOLUME /tmp
RUN touch /app.jar
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
  && apk update \
  && apk upgrade \
  && apk --no-cache add ttf-dejavu fontconfig
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]