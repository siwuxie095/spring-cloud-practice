package com.siwuxie095.spring.cloud.extend1st.example6th;

/**
 * @author Jiajing Li
 * @date 2021-06-27 13:19:24
 */
public class Main {

    /**
     * 构建 Docker 镜像
     *
     * 在构建过程中，这里的所有服务都被打包为 Docker 镜像。这个过程由 Spotify Maven 插件执行。有关此插件
     * 的实战示例，读者可以查看第三部分许可证服务的 pom.xml 文件。如下代码展示了在每个服务的 pom.xml 文件
     * 中配置此插件的 XML 片段。
     *
     * <plugin>
     *   <groupId>com.spotify</groupId>
     *   <artifactId>docker-maven-plugin</artifactId>
     *   <version>0.4.10</version>
     *   <configuration>
     *     <imageName>
     *       ${docker.image.name}:
     *       [ca]${docker.image.tag}　　⇽---　 创建的每个 Docker 镜像都会有一个与之关联的标签。Spotify 插件
     *                                        将使用 ${docker.image.tag} 标签中定义的名称命名创建的镜像
     *     </imageName>
     *     <dockerDirectory>
     *
     *       ${basedir}/target/dockerfile　　⇽---　 这里的所有 Docker 镜像都是使用 Dockerfile 创建的。
     *                                             Dockerfile 用于详细说明如何提供 Docker 镜像
     *     </dockerDirectory>
     *     <resources>
     *       <resource>
     *         <targetPath>/</targetPath>
     *         <directory>${project.build.directory}</directory>　　⇽---　 当执行 Spotify 插件时，它会将服务
     *                                                                    的可执行 jar 复制到 Docker 镜像中
     *         <include>${project.build.finalName}.jar</include>
     *       </resource>
     *     </resources>
     *   </configuration>
     * </plugin>
     *
     * 这个 XML 片段做了以下三件事。
     * （1）它将服务的可执行 jar 和 src/main/docker 目录的内容复制到 target/docker。
     * （2）它执行 target/docker 目录中定义的 Dockerfile。Dockerfile 是一个命令列表，每当为该服务提供
     * 新 Docker 镜像时，就会执行这些命令。
     * （3）它将 Docker 镜像推送到在安装 Docker 时安装的本地 Docker 镜像库。
     *
     * 如下代码展示了许可证服务的 Dockerfile 的内容。
     *
     * FROM openjdk:8-jdk-alpine　　⇽---　 这是在 Docker 运行时使用的 Linux Docker 镜像。此安装对 Java 应用程序进行了优化
     * RUN apk update && apk upgrade && apk add netcat-openbsd　　⇽---　 安装 nc（netcat），可以使用这个实用工具 ping
     *                                                                  依赖服务，以查看它们是否已启动
     * RUN mkdir -p /usr/local/licensingservice
     * ADD licensing-service-0.0.1-SNAPSHOT.jar /usr/local/licensingservice/　　⇽---　 Docker ADD 命令将可执行 JAR
     *                                                                              从本地文件系统复制到 Docker 镜像
     * ADD run.sh run.sh　　⇽---　 添加了一个自定义 BASH shell 脚本，它将监视服务依赖项，然后启动实际服务
     * RUN chmod +x run.sh
     * CMD ./run.sh
     *
     * 在这段代码所示的 Dockerfile 中，将使用 Alpine Linux 提供实例。Alpine Linux 是一个小型 Linux
     * 发行版，常用来构建 Docker 镜像。正在使用的 Alpine Linux 镜像已经安装了 Java JDK。
     *
     * 在提供 Docker 镜像时，将安装名为 nc 的命令行实用程序。nc 命令用于 ping 服务器并查看特定的端口是否
     * 在网络上可用。该命令将在 run.sh 命令脚本中使用，以确保在启动服务之前，所有依赖的服务（如数据库和
     * Spring Cloud Config 服务）都已启动。nc 命令通过监视依赖的服务监听的端口来做到这一点。nc 的安装是
     * 通过 RUN apk update && apk upgrade && apk add netcat-openbsd 完成的，使用 Docker Compose
     * 运行服务。
     *
     * 接下来，Dockerfile 将为许可证服务的可执行 JAR 文件创建一个目录，然后将 jar 文件从本地文件系统复制
     * 到在 Docker 镜像上创建的目录中。这都是通过 ADD licensing-service-0.0.1-SNAPSHOT.jar /usr
     * /local/licensingservice/ 完成的。
     *
     * 配置过程的下一步是通过 ADD 命令安装 run.sh 脚本。run.sh 脚本是这里写的一个自定义脚本，它用于在启
     * 动 Docker 镜像时启动目标服务。该脚本使用 nc 命令来监听许可证服务所需的所有关键服务依赖项的端口，
     * 然后阻塞许可证服务，直到这些依赖项都已启动。
     *
     * 如下代码展示了如何使用 run.sh 来启动许可证服务。
     *
     * #!/bin/sh
     * echo "********************************************************"
     * echo "Waiting for the configuration server to start on port
     *      $CONFIGSERVER_PORT"
     * echo "********************************************************"
     * while ! 'nc -z configserver $CONFIGSERVER_PORT ';
     *    [ca]do sleep 3; done　　⇽---　 在继续尝试启动服务之前，run.sh 脚本会等待依赖服务的端口处于打开状态
     * echo ">>>>>>>>>>>> Configuration Server has started"
     *
     * echo "********************************************************"
     * echo "Waiting for the database server to start on port $DATABASESERVER_PORT"
     * echo "********************************************************"
     * while ! 'nc -z database $DATABASESERVER_PORT'; do sleep 3; done
     * echo ">>>>>>>>>>>> Database Server has started"
     *
     * echo "********************************************************"
     * echo "Starting License Server with Configuration Service :
     *      $CONFIGSERVER_URI";
     * echo "********************************************************"
     * java -Dspring.cloud.config.uri=$CONFIGSERVER_URI \　　⇽---　 通过使用 Java 启动许可证服务，来调用 Dockerfile
     *                                       脚本安装的可执行 JAR 文件。$<<变量名称>> 代表传递给 Docker 镜像的环境变量
     *      -Dspring.profiles.active=$PROFILE \
     *      -jar /usr/local/licensingservice/licensing-service-0.0.1-SNAPSHOT.jar
     *
     * 一旦将 run.sh 命令复制到许可证服务的 Docker 镜像，Docker 命令 CMD./run.sh 用于告知 Docker 在
     * 实际镜像启动时执行 run.sh 启动脚本。
     */
    public static void main(String[] args) {

    }

}
