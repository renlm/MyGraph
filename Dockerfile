FROM registry.cn-hangzhou.aliyuncs.com/rlm/tomcat

COPY ROOT.war /usr/local/tomcat/webapps

WORKDIR /usr/local/tomcat
 
EXPOSE 8080

CMD ["/bin/bash", "-c", "bin/catalina.sh start && tail -f logs/catalina.out"]