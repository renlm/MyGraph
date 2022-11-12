FROM registry.cn-hangzhou.aliyuncs.com/rlm/tomcat

COPY target/ROOT.war /usr/local/tomcat/webapps

# https://github.com/krallin/tini
ENV TINI_VERSION v0.19.0
ADD https://renlm.gitee.io/download/tini/${TINI_VERSION}/tini
RUN chmod +x /tini
ENTRYPOINT ["/tini", "--"]
 
EXPOSE 8080

CMD ["bin/catalina.sh start && tail -f logs/catalina.out"]