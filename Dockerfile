FROM registry.cn-hangzhou.aliyuncs.com/rlm/tomcat

COPY target/ROOT.war /usr/local/tomcat/webapps

ENV TINI_VERSION v0.19.0
ADD https://renlm.gitee.io/download/tini/${TINI_VERSION}/tini /tini
RUN chmod +x /tini
ENTRYPOINT ["/tini", "--"]

CMD ["/bin/bash", "-c", "/usr/local/tomcat/bin/catalina.sh start"]
 
EXPOSE 8080