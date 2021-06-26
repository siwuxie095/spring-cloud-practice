package com.siwuxie095.spring.cloud.chapter10th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-26 22:32:41
 */
public class Main {

    /**
     * 构建和部署管道实战
     *
     * 从之前介绍的通用架构中可以看到，在构建/部署管道背后有许多活动部件。由于这里的目的是 "在实战中" 介绍
     * 知识，所以将详细介绍为 EagleEye 服务实现构建/部署管道的细节。
     *
     * 如下列出了要用来实现这一管道的不同技术。
     * （1）GitHub：GitHub 是这里的源代码控制库。这里的所有应用程序代码都在 GitHub 中。选择 GitHub 作
     * 为源代码控制库出于两个原因：首先，不用管理和维护自己的 Git 源代码管理服务器；其次，GitHub 提供了
     * 各种各样的 Web 钩子和强大的基于 REST 的 API，用于将 GitHub 集成到构建过程中。
     * （2）Travis CI：Travis CI 是这里用于构建和部署 EagleEye 微服务，并提供 Docker 镜像的持续集成
     * 引擎。Travis CI 是一个基于云的、基于文件的 CI 引擎，它易于建立，并且与 GitHub 和 Docker 有着
     * 很强的集成能力。虽然 Travis CI 不像 Jenkins 这样的 CI 引擎功能那么全面，但对使用来说已经足够了。
     * （3）Maven/Spotify Docker 插件：虽然这里使用 vanilla Maven 编译、测试和打包 Java 代码，但使
     * 用的一个关键 Maven 插件是 Spotify 的 Docker 插件，这个插件允许从 Maven 内部启动 Docker 构建
     * 的创建。
     * （4）Docker：这里选择 Docker 作为容器平台出于两个原因。首先，Docker 在多个云服务提供商之间是可移
     * 植的。可以采用相同的 Docker 容器，并以最少的工作将其部署到 AWS、Azure 或 Cloud Foundry。其次，
     * Docker 是轻量级的。（PS：在这里，你将会构建并部署大约 10 个 Docker 容器（包括数据库服务器、消息
     * 传递平台和搜索引擎）。在本地桌面上部署相同数量的虚拟机将是很困难的，因为每个镜像的规模大，并且需要
     * 的运行速度高。）
     * （5）Docker Hub：构建完服务并创建了 Docker 镜像之后，需要使用唯一的标识符对 Docker 镜像进行标记，
     * 并将它推送到中央存储库。对于 Docker 镜像存储库，这里选择使用 Docker Hub，即 Docker 公司的公共镜
     * 像存储库。
     * （6）Python：为了编写在部署 Docker 镜像之前执行的平台测试，这里选择了 Python 作为编写平台测试的
     * 工具。在工作中应使用合适的工具。坦率地说，Python 是一种非常棒的编程语言，特别是对于编写基于 REST
     * 的测试用例。
     * （7）Amazon 的 EC2 容器服务（ECS）：这里的微服务的最终目标是将 Docker 实例部署到亚马逊的 Docker
     * 平台。这里选择亚马逊作为云平台，是因为它是迄今为止最成熟的云提供商，它能让 Docker 服务的部署变得十
     * 分简单。
     *
     *
     * PS：等等，你说 Python 什么
     *
     * 你可能会觉得奇怪，这里用 Python 编写平台测试，而不是用 Java。这里是故意这么做的。Python
     * （就像Groovy 一样）是编写基于 REST 的测试用例的绝妙脚本语言。在工作中应使用合适的工具。
     * 对采用微服务的组织来说，最大的思想转变之一是，选择语言的职责应该在开发团队中。已经在太多的
     * 组织中目睹过对标准的教条式拥护（如：XX 企业标准是 Java ……，所有的代码都必须用 Java 编写）。
     * 因此，当 10 行的 Groovy 或 Python 脚本就可以完成这个工作时，就有开发团队跳过这一选择，
     * 转而编写了一大堆 Java 代码。
     *
     * 这里选择 Python 的第二个原因是，与单元测试和集成测试不同，平台测试是真正的 "黑盒" 测试，
     * 开发人员的行为就像在真实环境中运行的实际 API 的消费者。单元测试运行最低级别的代码，运行
     * 时不应该有任何外部依赖。集成测试上升了一个级别，它负责测试 API，但是需要 stub 或 mock
     * 关键的外部依赖项（如对其他服务的调用、数据库调用等）。平台测试应该是真正独立于底层基础
     * 设施的测试。
     */
    public static void main(String[] args) {

    }

}
