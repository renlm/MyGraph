FROM registry.cn-hangzhou.aliyuncs.com/rlm/tomcat

COPY ROOT.jar /usr/local/tomcat/webapps
 
EXPOSE 8080

CMD ["tail -f /usr/local/tomcat/logs/catalina.log"]