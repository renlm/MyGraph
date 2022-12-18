FROM registry.cn-hangzhou.aliyuncs.com/rlm/tomcat

COPY target/ROOT.war /usr/local/tomcat/webapps

ENV TINI_VERSION v0.19.0
ADD https://renlm.cn/download/tini/${TINI_VERSION}/tini /tini
RUN chmod +x /tini
ENTRYPOINT ["/tini", "--"]

CMD ["/bin/bash", "-c", "/usr/local/tomcat/bin/catalina.sh start && tail -f /usr/local/tomcat/logs/catalina.out"]
 
EXPOSE 8080