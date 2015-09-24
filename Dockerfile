# build maven project before creating this image
# mvn clean install after adapting the plugins used and the plugins included in the core module
# check the uriPattern.properties file or overwrite it as volume when running the docker

from  tomcat:jre7

MAINTAINER joeri moreno <joeri.moreno@tenforce.com>

RUN rm -rf webapps/*

COPY core/target/edcat-api-*.war webapps/ROOT.war
COPY ext/docker/setenv.sh bin/setenv.sh
RUN chmod a+x bin/setenv.sh
COPY ext/docker/sparql.properties /data/sparql.properties
COPY ext/docker/uriPattern.properties /data/uriPattern.properties


