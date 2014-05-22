set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8888,server=y,suspend=n
mvn clean jetty:run
