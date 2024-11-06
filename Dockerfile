FROM openjdk:17-slim
ADD . /build
WORKDIR /build
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
RUN --mount=type=cache,target=/root/.m2 \
  # https://mirrors.tuna.tsinghua.edu.cn/help/debian/
  cp -a /etc/apt/sources.list /etc/apt/sources.list.bak \
  && sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list \
  && sed -i 's/security.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list \
  && apt update \
  && apt install -y maven \
  && mvn clean package -P ${PROFILES_ACTIVE}

FROM openjdk:17-jdk-alpine
COPY --from=0 "/build/target/MyGraph.jar" app.jar
ADD file/msyh.ttf /usr/share/fonts/myfonts
ADD file/msyhbd.ttf /usr/share/fonts/myfonts
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
  && apk update \
  && apk upgrade \
  && apk --no-cache add fontconfig \
  && apk --no-cache add chromium chromium-chromedriver \
  && apk --no-cache add tini curl \
  && touch /app.jar
EXPOSE 8080
EXPOSE 9000
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["/sbin/tini","--","java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]