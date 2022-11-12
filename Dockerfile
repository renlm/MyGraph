FROM registry.cn-hangzhou.aliyuncs.com/rlm/tomcat

COPY target/ROOT.war /usr/local/tomcat/webapps

ADD https://renlm.gitee.io/download/tini/v0.19.0/tini /tini
RUN chmod +x /tini
 
EXPOSE 8080

CMD /tini -v -- /usr/local/tomcat/bin/catalina.sh start