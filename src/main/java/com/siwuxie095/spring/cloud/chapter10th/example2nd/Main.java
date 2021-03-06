package com.siwuxie095.spring.cloud.chapter10th.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-06-26 16:15:06
 */
public class Main {

    /**
     * EagleEye：在云中建立核心基础设施
     *
     * 在这里的所有代码示例中，将所有应用程序运行在一个虚拟机镜像中，其中每个单独的服务都是作为 Docker 容器运行的。
     * 现在要做一些改变，通过将数据库服务器（PostgreSQL）和缓存服务器（Redis）从 Docker 分离到亚马逊云中。所有
     * 其他服务将作为在单节点 Amazon ECS 集群中运行的 Docker 容器继续运行。
     *
     * 将 EagleEye 服务部署到亚马逊云的过程如下：
     * （1）所有核心 EagleEye 服务都将运行在单节点 ECS 集群中。
     * （2）数据库与 Redis 集群将迁移到亚马逊的服务中。
     * （3）ECS 容器的安全组设置限制所有入站端口流量，保证只有端口 5555 对公共流量开放。这意味着所有的 EagleEye
     * 服务只能通过监听 5555 端口的 Zuul 服务器来访问。
     * （4）组织服务和许可证服务受 OAuth2 验证服务保护。
     * （5）所有其他服务只能从 ECS 容器访问。
     *
     * PS：通过使用 Docker，所有的服务都可以部署到云服务提供商的环境中，如亚马逊的 ECS。
     *
     * 更多细节如下：
     * （1）所有的 EagleEye 服务（除了数据库和 Redis 集群）都将部署为 Docker 容器，这些 Docker 容器在单节点
     * ECS 集群内部运行。ECS 配置并建立运行 Docker 集群所需的所有服务器。ECS 还可以监视在 Docker 中运行的容器
     * 的健康状况，并在服务崩溃时重新启动服务。
     * （2）在部署到亚马逊云之后，将不再使用自己的 PostgreSQL 数据库和 Redis 服务器，而是使用亚马逊的 RDS 和
     * 亚马逊的 ElastiCache 服务。你可以继续在 Docker 中运行 Postgres 和 Redis 数据存储，但这里想强调的是，
     * 从自己拥有和管理的基础设施转移到由云供应商（在本例中是亚马逊）完全管理的基础设施非常容易。在实际部署中，在
     * Docker 容器出现之前，通常会将数据库基础设施部署到虚拟机上。
     * （3）与桌面部署不同，这里希望服务器的所有流量都通过 Zuul API 网关。这里将使用亚马逊安全组，仅允许已部署的
     * ECS 集群上的端口 5555 可供外界访问。
     * （4）这里仍将使用 Spring 的 OAuth2 服务器来保护服务。在可以访问组织服务和许可证服务之前，用户需要使用验
     * 证服务进行验证，并在每个服务调用中提供一个有效的 OAuth2 令牌。
     * （5）所有的服务器，包括 Kafka 服务器，外界都无法通过公开的 Docker 端口进行访问。
     *
     *
     * PS：实施前的必要准备
     *
     * 要建立亚马逊基础设施，你需要以下内容。
     * （1）自己的 Amazon Web Services（AWS）账户。你应该对 AWS 控制台和在该环境中工作的概念有一个基本的了解。
     * （2）一个 Web 浏览器。对于手动创建，你将从控制台创建所有内容。
     * （3）用于部署的亚马逊 ECS 命令行客户端。
     *
     * 如果你没有使用过 AWS，建议你建立一个 AWS 账户，并安装上面列出的工具，然后再花一些时间熟悉这个平台。
     *
     *
     *
     * 1、使用亚马逊的 RDS 创建 PostgreSQL 数据库
     *
     * 这里需要先创建和配置AWS账户，完成之后，第一项任务就是创建要用于 EagleEye 服务的 PostgreSQL 数据库。要做
     * 到这一点，将要登录到 AWS 控制台并执行以下操作。
     * （1）在第一次登录到控制台时，将看到一个亚马逊 Web 服务列表。找到 RDS 的链接并点击它，进入 RDS 仪表板。
     * （2）在仪表板上找到一个上面写着 "Launch a DB Instance" 的大按钮并点击它。
     * （3）RDS 支持不同的数据库引擎。此时，应该能看到一个数据库列表。选择 PostgreSQL，然后点击 "Select" 按钮。
     * 这将启动数据库创建向导。
     *
     * 亚马逊的数据库创建向导首先会询问这是生产数据库（Production）还是开发/测试（Dev/Test）数据库。这里将使用
     * 免费套餐创建开发/测试数据库。
     *
     * 接下来，将创建有关 PostgreSQL 数据库的基本信息，并设置将要使用的主用户 ID 和密码来登录数据库。
     *
     * 该向导的最后一步是创建数据库安全组、端口信息和数据库备份信息。
     *
     * 此时，数据库创建过程将开始（可能需要几分钟）。完成之后，需要配置 EagleEye 服务来使用数据库。创建完数据库
     * 之后（这需要几分钟），返回到 RDS 仪表板并查看创建的数据库。
     *
     * 这里为每个需要访问基于亚马逊的 PostgreSQL 数据库的微服务创建了一个名为 aws-dev 的新应用程序 profile。
     * 这里在 config-server 中添加了一个新的 Spring Cloud Config 服务器应用程序 profile，它包含亚马逊数
     * 据库连接信息。使用新数据库的每一个属性文件都遵循命名约定（服务名 ）-aws-dev.yml （许可证服务、组织服务
     * 和验证服务）。
     *
     * 此时，数据库已经准备好了（还不赖，只需要大约五次点击就能创建完成）。然后转向下一个应用程序基础设施，看看如何
     * 创建 EagleEye 许可证服务将要使用的 Redis 集群。
     *
     *
     *
     * 2、在 AWS 中创建 Redis 集群
     *
     * 要创建 Redis 集群，将要使用亚马逊的 ElastiCache 服务。ElastiCache 允许开发人员使用 Redis 或
     * Memcached 构建内存中的数据缓存。对于 EagleEye 服务，将把在 Docker 中运行的 Redis 服务器迁移
     * 到 ElastiCache。
     *
     * 先回到 AWS 控制台的主页（点击页面左上角的橙色立方体），然后点击 ElastiCache 链接。
     *
     * 在 ElastiCache 控制台中，选择 Redis 链接（页面的左侧），然后点击页面顶部的蓝色创建按钮。这将启动
     * ElastiCache/Redis 创建向导。
     *
     * 在填完所有数据后，点击 "Create" 按钮。ElastiCache 将开始 Redis 集群创建过程（这将需要几分钟的时间）。
     * ElastiCache 将在最小的亚马逊服务器实例上构建一个单节点的 Redis 服务器。一旦点击按钮，就会看到 Redis
     * 集群正在创建。创建完集群之后，点击集群的名称，进入详情页面，该页面显示集群中使用的端点。
     *
     * 许可证服务是唯一一个使用 Redis 的服务，因此如果你将这里的代码示例部署到自己的亚马逊实例中，一定要确保
     * 适当地修改许可证服务的 Spring Cloud Config 文件。
     *
     *
     *
     * 3、创建 ECS 集群
     *
     * 部署 EagleEye 服务之前的最后一步是创建 ECS 集群。建立一个 ECS 集群以供应要用于托管 Docker 容器的
     * Amazon 机器。要做到这一点，将再次访问 AWS 控制台。在这里，将点击 Amazon EC2 Container Service
     * 链接。
     *
     * 进入主 EC2 容器服务页面，在这里，应该会看到一个 "Getting Started" 按钮。点击 "Start" 按钮，进入
     * "Select options to configure" 页面。
     *
     * 取消勾选屏幕上的两个复选框，然后点击 "Cancel" 按钮。ECS 提供了一个向导，它基于一组预定义模板来创建
     * ECS 容器。这里不打算使用这个向导。一旦取消了 ECS 创建向导，应该会看到 ECS 主页上的 "Clusters" 选
     * 项卡。然后点击 "Create Cluster" 按钮开始创建 ECS 集群的过程。
     *
     * "Create Cluster" 界面有三个主要部分。第一部分将定义基本的集群信息。在这里需要输入以下信息：
     * （1）ECS 集群的名称；
     * （2）运行该集群的 Amazon EC2 虚拟机的大小；
     * （3）集群中运行的实例数；
     * （4）分配给集群中的每个节点的弹性块存储（Elastic Block Storage，EBS）的磁盘空间量。
     *
     * 注意：在创建 Amazon 账户时，首先要做的一件事是定义一个密钥对，用于使用 SSH 进入启动的 EC2 服务器中。
     * 这里不会介绍创建密钥对，但是如果你以前从未这样做过，建议你看看亚马逊有关这方面的说明书。
     *
     * 接下来，将要为 ECS 集群创建网络配置。进入 "Networking" 界面并进行配置。
     *
     * 首先要注意的是，选择 ECS 集群将运行的亚马逊的 Virtual Private Cloud（VPC）。默认情况下，ECS 设置
     * 向导将创建一个新的 VPC。这里已经选择在默认 VPC 中运行 ECS 集群。默认的 VPC 包含数据库服务器和 Redis
     * 集群。在亚马逊云中，亚马逊管理的 Redis 服务器只能由与 Redis 服务器处于同一个 VPC 的服务器访问。
     *
     * 接下来，必须在 VPC 中选择要为 ECS 集群提供访问权限的子网。因为每个子网对应于一个 AWS 可用区域，所以
     * 这里通常选择 VPC 中的所有子网，以使集群可用。
     *
     * 最后，必须选择创建一个新的安全组，或者选择已创建的现有 Amazon 安全组，以应用于新的 ECS 集群。因为这
     * 里正在运行 Zuul，并且希望所有的通信都通过单一端口（5555）。这里将要配置由 ECS 向导创建的新安全组，
     * 以允许来自外界的入站通信（0.0.0.0/0 是整个因特网的网络掩码）。
     *
     * 在表单中必须填写的最后一步是，为在服务器上运行的 ECS 容器代理创建 Amazon IAM 角色。ECS 代理负责与
     * Amazon 就服务器上运行的容器的状态进行通信。这里将允许 ECS 向导创建一个名为 ecsInstanceRole 的
     * IAM 角色。
     *
     * 此时，你应该能看到一个集群创建跟踪状态的界面。创建完集群之后，应该在界面上看到一个蓝色的名为 "View
     * Cluster" 按钮。
     *
     * 此时，已经具备了成功部署 EagleEye 微服务所需的所有基础设施。
     *
     *
     * PS：关于基础设施的创建和自动化
     *
     * 假设你现在正通过 AWS 控制台执行所有操作。在真实环境中，你可以使用亚马逊的 CloudFormation 脚本 DSL
     * （领域特定语言）或 HashCorp 的 Terraform 这样的云基础设施脚本工具创建所有这些基础设施。不过，这是
     * 一个完整的主题，它远远超出了这里的范围。如果你使用亚马逊云，那么可能已经熟悉 CloudFormation。如果你
     * 是亚马逊云的新手，那么建议你花一些时间去了解它，然后再通过 AWS 控制台创建核心基础设施。
     */
    public static void main(String[] args) {

    }

}
