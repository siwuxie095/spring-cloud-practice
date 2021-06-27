package com.siwuxie095.spring.cloud.extend1st.example7th;

/**
 * @author Jiajing Li
 * @date 2021-06-27 13:42:40
 */
public class Main {

    /**
     * 使用 Docker Compose 启动服务
     *
     * Maven 构建完成后，现在就可以使用 Docker Compose 来启动对应部分的所有服务了。Docker Compose
     * 作为 Docker 安装过程的一部分安装。Docker Compose 是一个服务编排工具，它允许开发人员将服务定义
     * 为一个组，然后作为一个单元一起启动。Docker Compose 还拥有为每个服务定义环境变量的功能。
     *
     * Docker Compose 使用 YAML 文件来定义将要启动的服务。这里的每一部分中都有一个名为 "/docker
     * /common/docker-compose.yml" 的文件。该文件包含在当前部分中启动服务的服务定义。下面来看一下第
     * 三部分中使用的 docker-compose.yml 文件。如下代码展示了这个文件的内容。
     *
     * version: '2'
     * services:
     *   configserver:　　⇽---　 每个正在启动的服务都会有一个标签。这个标签将成为 Docker 实例启动时的 DNS 条目，
     *                          其他服务将通过这个 DNS 条目访问这个服务
     *     image: johncarnell/tmx-confsvr:chapter3　　⇽---　 Docker Compose 将首先尝试在本地 Docker 存储
     *                                         库中查找要启动的目标镜像。如果找不到，它将检查中央 Docker Hub
     *     ports:
     *       - "8888:8888"　　⇽---　 这个条目定义了已启动的 Docker 容器上的端口号，这个端口将暴露给外部世界
     *     environment:
     *       ENCRYPT_KEY:       "IMSYMMETRIC"　　⇽---　 环境标签用于将环境变量传递到启动的 Docker镜像。在本例
     *                                               中，将在启动的 Docker 镜像上设置 ENCRYPT_KEY 环境变量
     *   database:
     *     image: postgres
     *     ports:
     *       - "5432:5432"
     *     environment:
     *       POSTGRES_USER: "postgres"
     *       POSTGRES_PASSWORD: "p0stgr@s"
     *       POSTGRES_DB: "eagle_eye_local"
     *   licensingservice:
     *     image: johncarnell/tmx-licensing-service:chapter3
     *     ports:
     *       - "8080:8080"
     *     environment:
     *       PROFILE: "default"
     *       CONFIGSERVER_URI: "http://configserver:8888"　　⇽---　 这是一个示例，说明在 Docker Compose
     *                                              文件的某个部分中定义的服务如何用作其他服务中的 DNS 名称
     *       CONFIGSERVER_PORT: "8888"
     *       DATABASESERVER_PORT: "5432"
     *       ENCRYPT_KEY: "IMSYMMETRIC"
     *
     * 在这段代码所示的 docker-compose.yml 中，看到定义了三个服务（configserver 、database 和
     * licensingservice）。每个服务都有一个使用 image 标签定义的 Docker 镜像。当每个服务启动时，
     * 它将通过 port 标签公开端口，然后通过 environment 标签将环境变量传递到启动的 Docker容器。
     *
     * 接下来，在从 GitHub 拉取的根目录执行以下命令来启动 Docker 容器：
     *
     * docker-compose –f docker/common/docker-compose.yml up
     *
     * 当这个命令发出时，docker-compose 启动 docker-compose.yml 文件中定义的所有服务。每个服务
     * 将打印其标准输出到控制台。
     *
     * 提示：使用 Docker Compose 启动的服务写入标准输出的每一行中都有打印到标准输出的服务的名称。
     * 启动 Docker Compose 时，发现打印出来的错误可能会感到很痛苦。如果你想查看基于 Docker 的
     * 服务的输出，可使用 -d 选项以分离模式启动 docker-compose 命令（docker-compose –f
     * docker/common/ docker-compose.yml up -d ）。然后，就可以通过使用 logs 选项发出
     * docker-compose 命令（docker-compose–f docker/common/docker-compose.yml logs–f
     * licensingservice）来查看该容器的特定日志。
     *
     * 所有在这里使用的 Docker 容器都是暂时的 —— 它们在启动和停止时不会保留它们的状态。如果你开始
     * 运行代码，那么在重启容器之后数据会消失，请牢记这一点。如果你想让自己的 Postgres 数据库在容
     * 器的启动和停止之间保持持久性，建议查阅 Postgres Docker 的资料。
     */
    public static void main(String[] args) {

    }

}
