FROM registry.cn-hangzhou.aliyuncs.com/rlm/tomcat

COPY ROOT.war /usr/local/tomcat/webapps
 
EXPOSE 8080

CMD ["/bin/bash","-c","/usr/local/tomcat/bin/catalina.sh start && tail -f /usr/local/tomcat/logs/catalina.out"]