FROM openjdk:17-jdk-alpine
ADD target/MyGraph.jar app.jar
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories \
  && apk update \
  && apk upgrade \
  && apk --no-cache add ttf-dejavu fontconfig \
  && apk --no-cache add chromium chromium-chromedriver \
  && apk --no-cache add tini \
  && touch /app.jar
EXPOSE 80
EXPOSE 9000
ENTRYPOINT ["/sbin/tini", "--", "java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]