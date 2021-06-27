package com.siwuxie095.spring.cloud.extend1st.example1st;

/**
 * @author Jiajing Li
 * @date 2021-06-27 12:08:23
 */
public class Main {

    /**
     * 在桌面运行云服务
     *
     * 在编写这里的代码示例和选择部署代码所需的运行时技术时，有两个目标。第一个目标是确保代码示例易于使用并且
     * 易于设置。请记住，一个微服务应用程序有多个移动部件，如果没有一些深谋远虑的话，要建立这些部件来用最小的
     * 工作量顺畅运行微服务可能会很困难。
     *
     * 第二个目标是让每一部分都是完全独立的，这样你就可以选择任何一部分，并拥有一个完整的运行时环境，它封装了
     * 运行这一部分的代码示例所需的所有服务和软件，而不依赖于其他部分。
     *
     * 为此，在这里的每一部分中都会用到下列技术和模式。
     * （1）所有项目都使用 Apache Maven 作为这一部分的构建工具。每个服务都是使用 Maven 项目结构构建的，每
     * 个服务的结构都是按各个部分组织的。
     * （2）每一部分中开发的所有服务都编译为 Docker 容器镜像。Docker 是一个非常出色的运行时虚拟化引擎，它
     * 能够运行在 Windows、OS X 和 Linux 上。使用 Docker，可以在桌面上构建一个完整的运行时环境，包括应用
     * 程序服务和支持这些服务所需的所有基础设施。此外，Docker 不像其他专有的虚拟化技术，Docker 可轻松跨多个
     * 云供应商进行移植。这里使用 Spotify 的 Docker Maven 插件将 Docker 容器的构建与 Maven 构建过程集成
     * 在一起。
     * （3）为了在编译成 Docker 镜像之后启动这些服务，这里使用 Docker Compose 以一个组来启动这些服务。这
     * 里有意避免使用更复杂的 Docker 编排工具，如 Kubernetes 或 Mesos，以保持各部分示例简单且可移植。
     *
     * 所有 Docker 镜像的提供都是通过简单的 shell 脚本完成的。
     */
    public static void main(String[] args) {

    }

}