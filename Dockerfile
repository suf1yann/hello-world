FROM tomcat:9.0-jdk17
COPY target/hello-world-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
