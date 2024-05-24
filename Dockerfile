FROM openjdk:17-jdk-alpine
ADD target/MyGraph.jar app.jar
RUN mkdir -p /usr/share/fonts/myfonts
COPY file/msyh.ttf /usr/share/fonts/myfonts
COPY file/msyhbd.ttf /usr/share/fonts/myfonts
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
  && apk update \
  && apk upgrade \
  && apk --no-cache add fontconfig \
  && apk --no-cache add chromium chromium-chromedriver \
  && apk --no-cache add tini \
  && touch /app.jar
EXPOSE 8080
EXPOSE 9000
ENTRYPOINT ["/sbin/tini","--","java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]