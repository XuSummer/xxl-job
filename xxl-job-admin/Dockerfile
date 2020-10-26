FROM openjdk:8-jre-slim
MAINTAINER xuxueli
VOLUME /data

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 对应pom.xml文件中的dockerfile-maven-plugin插件buildArgs配置项JAR_FILE的值
# 复制打包完成后的jar文件到/opt目录下
COPY ./target/xxl-job-admin-*.jar /opt/app.jar
# 启动容器时执行
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/app.jar"]
#ENTRYPOINT ["sh","-c","java -Djava.security.egd=file:/dev/./urandom -jar $JAVA_OPTS /app.jar $PARAMS"]
# 使用端口8080
EXPOSE 8090
