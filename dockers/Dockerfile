# 环境
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:21
ENV LANG C.UTF-8
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 作者信息
MAINTAINER Xinghao.Wang "yang.zhou"
# 将本地文件tmp挂载到容器
VOLUME /tmp
# 拷贝jar
ADD fraud.jar /fraud.jar
# 创建日志文件目录
RUN  mkdir -p /data/logs
# 设置暴露的端口号
EXPOSE 8080
# 执行命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /fraud.jar -Djava.security.egd=file:/dev/./urandom"]
