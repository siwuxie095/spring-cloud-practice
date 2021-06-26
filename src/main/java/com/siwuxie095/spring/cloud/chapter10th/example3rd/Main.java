package com.siwuxie095.spring.cloud.chapter10th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-06-26 21:30:00
 */
public class Main {

    /**
     * 超越基础设施：部署 EagleEye
     *
     * 目前已经建立了基础设施，现在将把 EagleEye 服务部署到 Amazon ECS 容器中。此工作将要分成两部分来完成。
     * 第一部分工作是为那些做事情做到最后丧失耐心的人而做的，将展示如何将 EagleEye 手动部署到 Amazon 实例
     * 中。这将有助于了解部署服务的机制，并查看在容器中运行的已部署服务。虽然自己动手手动地部署服务很有趣，但
     * 这是不可持续的也是不推荐的。
     *
     * 这就是第二部分工作发挥作用的地方。在第二部分工作中，将人类排除在构建和部署过程之外，使整个构建和部署过
     * 程自动化。这是目标结束状态。通过演示如何设计、构建和部署微服务到云，将会体验到这种目标状态要优于所介绍
     * 的手工方式。
     *
     *
     *
     * 手动将 EagleEye 服务部署到 ECS
     *
     * 要手动部署 EagleEye 服务，要切换一下，离开 AWS 控制台。为了部署 EagleEye 服务，将使用亚马逊的 ECS
     * 命令行客户端（https://github.com/aws/amazon-ecs-cli ）。安装完 ECS 命令行客户端之后，需要配置
     * ecs-cli 运行时环境，从而完成以下工作。
     * （1）使用亚马逊凭据来配置 ECS 客户端。
     * （2）选择客户端将要工作的区域。
     * （3）定义 ECS 客户端将使用的默认 ECS 集群。
     * （4）通过运行 ecs-cli configure 命令来完成这项工作：
     *      ecs-cli configure --region us-west-1 \
     *                   --access-key $AWS_ACCESS_KEY \
     *                   --secret-key $AWS_SECRET_KEY \
     *                   --cluster spmia-tmx-dev
     *
     * ecs-cli configure 命令将设置集群所在的区域、亚马逊的 AWS 访问密钥和私密密钥，以及已部署集群的名称
     * （spmia-tmx-dev）。如果你查看上述命令，会发现这里在使用环境变量来保存亚马逊的访问密钥和私密密钥
     * （$AWS_ACCESS_KEY 和 $AWS_SECRET_KEY）。
     *
     * 注意：这里选择 us-west-1 地区纯粹是为了说明。你可以根据自己所在国家的不同，选择一个更具体的 AWS 地区。
     *
     * 接下来，看看如何进行构建。与之前不同的是，必须要设置构建名称，因为这里的 Maven 脚本将在后续建立的构建
     * 部署管道中使用。这里将要设置一个名为 $BUILD_NAME 的环境变量。$BUILD_NAME 环境变量用于标记由构建脚
     * 本创建的 Docker 镜像。切换代码的根目录，并执行以下两条命令：
     *
     * export BUILD_NAME=TestManualBuild
     * mvn clean package docker:build
     *
     * 这将使用位于项目目录的根目录下的父 POM 来执行 Maven 构建。建立父 pom.xml 来构建将在这里部署的所有
     * 服务。Maven 代码执行完成后，可以将 Docker 镜像部署到之前建立的 ECS 实例。要进行部署，应执行以下命
     * 令：
     *
     * ecs-cli compose --file docker/common/docker-compose.yml up
     *
     * ECS命令行客户端允许开发人员使用 Docker-compose 文件部署容器。通过允许复用来自桌面开发环境的
     * Docker-compose 文件，亚马逊已经大大简化了将服务部署到 Amazon ECS 的工作。在 ECS 客户端运
     * 行后，可以通过执行以下命令来确认服务正在运行，并发现服务器的 IP 地址：
     *
     * ecs-cli ps
     *
     * 从 ecs-cli ps 命令的输出结果可以看到：
     * （1）已经部署了 7 个 Docker 容器，每个 Docker 容器都运行一个服务。
     * （2）ECS 集群的 IP 地址（54.153.122.116）。
     * （3）除了端口 5555 以外还打开了其他端口。然而事实并非如此。这里的端口标识符是 Docker 容器的
     * 端口映射。但是，对外界开放的唯一端口是端口 5555。记住，在创建 ECS 集群时，ECS 创建向导创建了
     * 一个亚马逊安全组，该安全组只允许来自端口 5555 的流量。
     *
     * 此时已经成功将第一组服务部署到 Amazon ECS 客户端。后续将看一下如何设计一个构建和部署管道，以便将服务
     * 编译、打包和部署到亚马逊云的过程自动化。
     *
     *
     * PS：通过调试找出 ECS 容器无法启动或无法保持运行的原因
     *
     * ECS 没有多少工具可用于调试容器无法启动的原因。如果 ECS 部署的服务在启动或保持运行时遇到问题，就需要
     * 通过 SSH 进入 ECS 集群来查看 Docker 日志。为此，需要将端口 22 添加到 ECS 集群所运行的安全组，然
     * 后使用在设置集群时定义的亚马逊密钥对，以 ec2 用户身份通过 SSH 进入。一旦进入了服务器，就可以通过运
     * 行 docker ps 命令来获得在服务器上运行的所有 Docker 容器的列表。找到要调试的容器镜像后，可以运行
     * "docker logs –f 容器 ID" 命令来追踪目标 Docker 容器的日志。
     */
    public static void main(String[] args) {

    }

}
