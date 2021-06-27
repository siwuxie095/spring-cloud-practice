package com.siwuxie095.spring.cloud.chapter10th.example7th;

/**
 * @author Jiajing Li
 * @date 2021-06-26 22:55:42
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使服务能够在 Travis CI 中构建
     *
     * 在这里构建的每个服务的核心都是一个 Maven pom.xml 文件，它用于构建 Spring Boot 服务、将服务打包到
     * 可执行 JAR 中，然后构建可用于启动服务的 Docker 镜像。到目前为止，服务的编译和启动都是通过以下步骤来
     * 完成。
     * （1）在本地机器上打开一个命令行窗口。
     * （2）运行对应的 Maven 脚本。这将构建所有服务，然后将它们打包成一个 Docker 镜像，并将该镜像推送到本
     * 地运行的 Docker 存储库。
     * （3）从本地 Docker 存储库启动新创建的 Docker 镜像，方法是使用 docker-compose 和 docker-machine
     * 启动对应的所有服务。
     *
     * 问题是，如何在 Travis CI 中重复这个过程？这一切都是从一个名为 .travis.yml 的文件开始。.travis.yml
     * 是一个基于 YAML 的文件，它描述了当 Travis CI 执行构建时开发人员想要采取的行动。这个文件存储在微服务
     * 的 GitHub 存储库的根目录下。
     *
     * 当一个提交发生在 Travis CI 正在监视的 GitHub 存储库上时，Travis CI 将查找 .travis.yml 文件，然
     * 后启动构建过程。
     *
     * 如下是当一个提交发生在用于保存代码的 GitHub 存储库时，.travis.yml 文件将执行的步骤。
     * （1）开发人员对 GitHub 存储库中的一个微服务进行了更改。
     * （2）GitHub 通知 Travis CI 发生了一个提交。当注册了 Travis 并提供了自己的 GitHub 账户通知时，这
     * 个通知配置就会无缝地进行。Travis CI 将启动一个虚拟机，用于执行构建。然后，Travis CI 将从 GitHub
     * 中签出源代码，然后使用 .travis.yml 文件开始整个构建和部署过程。
     * （3）Travis CI 在构建中创建基本配置并安装依赖项。基本配置包括将在构建中使用哪种编程语言（Java）、是
     * 否需要 Sudo 执行软件安装和访问 Docker（用于创建和标记 Docker 容器）、设置在构建中所需的 secure 环
     * 境变量，以及定义如何通知构建的成功或失败。
     * （4）在实际构建执行之前，作为构建过程的一部分，可以指示 Travis CI 安装可能需要的任何第三方库或命令行
     * 工具。这里使用 travis 和亚马逊的 ecs-cli （EC2 容器服务客户端）这两个命令行工具。
     * （5）对于构建过程，总是先在源代码库中标记代码，以便在将来的任何时候都可以根据构建的标签提取源代码的完
     * 整版本。
     * （6）构建过程接下来将执行服务的 Maven 脚本。Maven 脚本将编译 Spring 微服务、运行单元测试和集成测试，
     * 然后基于该构建来构建一个 Docker 镜像。
     * （7）一旦构建的 Docker 镜像完成，构建过程会使用与标记源代码存储库相同的标签名称，将镜像推送到 Docker
     * Hub。
     * （8）然后构建过程将使用项目的 docker-compose 文件和亚马逊的 ecs-cli 将构建的所有服务部署到亚马逊的
     * Docker 服务 —— ECS。
     * （9）一旦服务部署完成，构建过程将启动一个完全独立的 Travis CI 项目，该项目将针对开发环境运行平台测试。
     *
     * 现在已经看完 .travis.yml 文件中涉及的一般步骤，下面来看看 .travis.yml 文件的细节。如下代码展示了
     * .travis.yml 文件的不同部分。
     *
     * language: java　　⇽---　 3．为构建创建核心运行时配置
     * jdk:
     *   - oraclejdk8
     * cache:
     *   directories:
     *     - "$HOME/.m2"
     * sudo: required
     * services:
     *   - docker
     * notifications:　　⇽---　 3．为构建创建核心运行时配置
     *   email:
     *   - youremail@gmail.com
     *   on_success: always
     *   on_failure: always
     * branches:　　⇽---　 3．为构建创建核心运行时配置
     *   only:
     *     - master
     * env:　　⇽---　 3．为构建创建核心运行时配置
     *   global:
     *   # 为了简洁，省略了部分内容
     * before_install: 　　⇽---　 4．执行所需的命令行工具的预构建安装
     *   - gem install travis -v 1.8.5 --no-rdoc --no-ri
     *   - sudo curl -o /usr/local/bin/ecs-cli
     *     ➥ https://s3.amazonaws.com/amazon-ecs-cli/
     *     ➥ ecs-cli-linux-amd64-latest
     *   - sudo chmod +x /usr/local/bin/ecs-cli
     *   - export BUILD_NAME=chapter10-$TRAVIS_BRANCH-
     *     ➥ $(date -u "+%Y%m%d%H%M%S")-$TRAVIS_BUILD_NUMBER
     *   - export CONTAINER_IP=52.53.169.60
     *   - export PLATFORM_TEST_NAME="chapter10-platform-tests"
     * script:
     *   - sh travis_scripts/tag_build.sh　　⇽---　 5．执行一个 shell 脚本，它将使用构建名标记源代码
     *   - sh travis_scripts/build_services.sh　　⇽---　 6．使用 Maven 构建服务器和本地 Docker 镜像
     *   - sh travis_scripts/deploy_to_docker_hub.sh　　⇽---　 7．将 Docker 镜像推送到 Docker Hub
     *   - sh travis_scripts/deploy_amazon_ecs.sh　　⇽---　 8．在 Amazon ECS 容器中启动服务
     *   - sh travis_scripts/trigger_platform_tests.sh　　⇽---　 9．触发一个 Travis 构建，为构建服务执行平台测试
     *
     * 下面将详细介绍构建过程中涉及的每一个步骤。
     *
     *
     *
     * 1、构建的核心运行时配置
     *
     * .travis.yml 文件的第一部分处理 Travis 构建的核心运行时配置。通常 .travis.yml 文件的这部分将包含
     * 特定 Travis 的功能，如：
     * （1）告诉 Travis 开发工作使用的编程语言；
     * （2）定义构建过程是否需要 Sudo 访问权限；
     * （3）定义在构建过程中是否要使用 Docker；
     * （4）声明将要使用的 secure 环境变量。
     *
     * 如下代码展示了构建文件的这部分配置。
     *
     * language: java　　⇽---　 ❶ 告诉 Travis 在主要运行时环境中使用 Java 和 JDK 8
     * jdk:
     *   - oraclejdk8
     * cache:　　⇽---　 ❷ 告诉 Travis 在构建之间缓存和复用 Maven 目录
     *   directories:
     *     - "$HOME/.m2"
     * sudo: required　　⇽---　 ❸ 允许构建在正在运行的虚拟机上使用 Sudo 访问
     * services:
     *   - docker
     * notifications:　　⇽---　 ❹ 配置用于通知构建成功或失败的电子邮件地址
     *   email:
     *   - youremail@gmail.com
     *   on_success: always
     *   on_failure: always
     * branches:　　⇽---　 ❺ 指示 Travis，它应该只在主分支有提交情况下进行构建
     *   only:
     *     - master
     * env:　　⇽---　 ❻ 在脚本中创建 secure 环境变量
     *   global:
     *     -secure: IAs5WrQIYjH0rpO6W37wbLAixjMB7kr7DBAeWhjeZFwOkUMJbfuHNC=z…
     *     # 为了简洁，省略了其他代码
     *
     * Travis 构建脚本的第一件事就是告诉 Travis 使用哪种主要语言来完成构建。通过将 language 指定为 java
     * 和将 jdk 属性指定为 oraclejdk8 ❶，Travis 将确保为项目安装和配置 JDK。
     *
     * .travis.yml 文件的下一部分，即 cache.directories 属性 ❷，告诉 Travis，在执行构建时缓存此目录的
     * 结果，并在多个构建中复用它。在处理像 Maven 这样的包管理器时是非常有用的，因为每次构建启动都需要花费
     * 大量时间来下载 jar 依赖项的新副本。如果没有设置 cache.directories 属性，则这里的构建可能需要花费
     * 10 min 的时间来下载所有相关的 jar 文件。
     *
     * 接下来的两个属性是 sudo 属性和 services 属性 ❸。sudo 属性用于告诉 Travis，构建过程需要使用 sudo
     * 作为构建的一部分。UNIX sudo 命令用于临时提升用户权限到 root 权限。通常来说，在需要安装第三方工具时
     * 使用 sudo。当需要安装 Amazon ECS 工具时，确实需要在构建中使用 sudo。
     *
     * services 属性用于告诉 Travis，在执行构建时是否要使用某些关键服务。例如，如果集成测试需要本地数据库
     * 供其运行，则 Travis 允许开发人员在构建中直接启动 MySQL 或 PostgreSQL 数据库。在这个例子中，需要
     * 运行 Docker 为每个 EagleEye 服务构建 Docker 镜像，并将镜像推送到 Docker Hub。这类已经将 services
     * 属性设置为在构建启动时启动 Docker。
     *
     * 下一个属性 notifications ❹ 定义了构建成功或失败时使用的通信通道。现在，始终通过将构建的通知通道设
     * 置为电子邮件来传达构建结果。Travis 会通过电子邮件通知构建的成功与失败。此外，Travis CI 可以通过除
     * 电子邮件以外的多种通道进行通知，包括 Slack、IRC、HipChat 或自定义 Web 钩子。
     *
     * branches.only 属性 ❺ 告诉 Travis，应该针对什么分支进行构建。对于这里的示例，只需完成 Git 的
     * master 分支的构建。这样可以防止每次在 GitHub 中标记存储库或提交代码到分支时都启动构建。这一点
     * 很重要，因为每次标记存储库或创建发布时，GitHub 都会对 Travis 进行回调。branch.only 属性设置
     * 为 master 以防止 Travis 陷入无休止的构建。
     *
     * 构建配置的最后一部分是设置敏感的环境变量 ❻。在构建过程中，可能会与第三方供应商（如Docker、GitHub
     * 和 Amazon）进行通信。有时通过它们的命令行工具进行通信，而其他时候则是使用 API。无论如何，经常需要
     * 出示敏感的凭据。Travis CI 能够让开发人员添加加密的环境变量来保护这些凭据。
     *
     * 要添加一个加密的环境变量，必须在包含源代码的项目目录中使用桌面上的 travis 命令行工具对环境变量进行
     * 加密。要在本地安装 Travis 命令行工具，可查阅该工具的官方文档。对于这里使用的 .travis.yml，创建并
     * 加密了以下环境变量。
     * （1）DOCKER_USERNAME：Docker Hub 用户名。
     * （2）DOCKER_PASSWORD：Docker Hub 密码。
     * （3）AWS_ACCESS_KEY：亚马逊的 ecs-cli 命令行客户端使用的 AWS 访问密钥。
     * （4）AWS_SECRET_KEY：亚马逊的 ecs-cli 命令行客户端使用的 AWS 私密密钥。
     * （5）GITHUB_TOKEN：GitHub 生成的令牌，用于指示允许调入的应用程序对服务器执行的访问级别。
     * 这个令牌必须先用 GitHub 应用程序生成。
     *
     * 一旦安装了 travis 工具，以下命令就会将加密的环境变量 DOCKER_USERNAME 添加到 .travis.yml 文件的
     * env.global 部分：
     *
     * travis encrypt DOCKER_USERNAME=somerandomname --add env.global
     *
     * 运行此命令后，现在应在 .travis.yml 文件的 env.global 部分中看到一个 secure 属性标签，后面是一长
     * 串文本。但是，Travis 不会在 .travis.yml 文件中标记加密环境变量的名字。
     *
     * 注意：加密的变量只适用于它们加密所在的单个 GitHub 存储库，并且 Travis 是针对这个 GitHub 存储库进
     * 行构建的。不能采用剪切加密环境变量并在多个 .travis.yml 文件中进行粘贴的这种方式。如果你这么做，构
     * 建将无法运行，因为加密的环境变量不能正确解密。
     *
     *
     * PS：不管构建工具是什么，要始终加密凭据
     *
     * 尽管所有的例子都使用 Travis CI 作为构建工具，但所有现代构建引擎都允许开发人员加密凭据和令牌。请务必
     * 确保加密凭据。在源代码存储库中嵌入的凭据是一个常见的安全漏洞。不要因为相信源代码控制库是安全的，就相
     * 信它里面的凭据是安全的。
     *
     *
     *
     * 2、安装预构建工具
     *
     * 预构建的配置居然有那么多，而下一部分的配置却很少。构建引擎通常包含大量 "胶水代码" 脚本，用于将构建过
     * 程中使用的不同工具联系在一起。使用上述 Travis 脚本，需要安装以下两个命令行工具。
     * （1）travis：这个命令行工具用于与 Travis 构建进行交互。后续将使用它来检索 GitHub 令牌，以编程方式
     * 触发另一个 Travis 构建。
     * （2）ecs-cli：这是用于与 Amazon ECS 交互的命令行工具。
     *
     * .travis.yml 文件的 before_install 部分中列出的每一项都是一个 UNIX 命令，这些命令将在构建启动之
     * 前执行。如下所示。
     *
     * before_install:
     *   - gem install travis -v 1.8.5 --no-rdoc --no-ri　　⇽---　 安装 Travis 命令行工具
     *   - sudo curl -o /usr/local/bin/ecs-cli
     *   ➥  https://s3.amazonaws.com/amazon-ecs-cli/　　⇽---　 安装亚马逊的 ECS 客户端
     *   ➥  ecs-cli-linux-amd64-latest
     *   - sudo chmod +x /usr/local/bin/ecs-cli　　⇽---　 在 ECS 客户端将权限更改为可执行
     *   - export BUILD_NAME=chapter10-$TRAVIS_BRANCH-　　⇽---　 设置在构建过程中使用的环境变量
     *   ➥  $(date -u "+%Y%m%d%H%M%S")-$TRAVIS_BUILD_NUMBER
     *
     *   - export CONTAINER_IP=52.53.169.60
     *   - export PLATFORM_TEST_NAME="chapter10-platform-tests"
     *
     * 在构建过程中要做的第一件事，是在远程构建服务器上安装 travis 命令行工具：
     *
     * gem install travis -v 1.8.5 --no-rdoc --no-ri
     *
     * 在稍后的构建过程中，将通过 Travis REST API 启动另一个 Travis 作业。这里需要使用 travis 命令行工
     * 具来获取用于调用此 REST 调用的令牌。
     *
     * 安装完 travis 工具之后，将安装亚马逊的 ecs-cli 工具。这个命令行工具用于部署、启动和停止在亚马逊云
     * 内部运行的 Docker 容器。
     *
     * 首先下载二进制文件，然后将下载的二进制文件的权限更改为可执行文件来安装 ecs-cli：
     *
     * - sudo curl -o /usr/local/bin/ecs-cli https://s3.amazonaws.com/amazon-ecs-cli/
     * ➥  ecs-cli-linux-amd64-latest
     * - sudo chmod +x /usr/local/bin/ecs-cli
     *
     * 在 .travis.yml 的 before_install 部分完成的最后一件事是在构建中设置三个环境变量。这三个环境变量
     * 将有助于驱动构建的行为。这些环境变量如下：
     * （1）BUILD_NAME；
     * （2）CONTAINER_IP；
     * （3）PLATFORM_TEST_NAME。
     *
     * 在这些环境变量中设置的实际值如下：
     *
     * - export BUILD_NAME=chapter10-$TRAVIS_BRANCH-
     * ➥  $(date -u "+%Y%m%d%H%M%S")-$TRAVIS_BUILD_NUMBER
     * - export CONTAINER_IP=52.53.169.60
     * - export PLATFORM_TEST_NAME="chapter10-platform-tests"
     *
     * 第一个环境变量 BUILD_NAME 生成一个唯一的构建名称，该名称包含构建的名称，后面是日期和时间（直到秒字
     * 段），然后是 Travis 中的构建编号。这个 BUILD_NAME 将用于在 Docker 镜像被推送到 Docker Hub 存
     * 储库时，对 Docker 镜像以及 GitHub 中的源代码进行标记。
     *
     * 第二个环境变量 CONTAINER_IP 包含 Amazon ECS 虚拟机的 IP 地址，Docker 容器将运行在该 Amazon
     * ECS 虚拟机上。这个 CONTAINER_IP 稍后将被传递到另一个 Travis CI 作业，它将执行平台测试。
     *
     * 注意：这里并没有将静态 IP 地址分配给 Amazon ECS 服务器。如果彻底拆除容器，会得到一个新的 IP。在实
     * 际生产环境中，ECS 集群中的服务器可能会被分配静态（不变）IP，并且集群将具有 Amazon 企业负载均衡器
     * （Enterprise Load Balancer，ELB）和 Amazon Route 53 DNS 名称，以便 ECS 服务器的实际 IP 地址
     * 对服务是透明的。但是，建立这么多的基础设施超出了这里演示的示例的范围。
     *
     * 第三个环境变量 PLATFORM_TEST_NAME 包含正在执行的构建作业的名称。
     *
     *
     * PS：关于审查与可追溯性
     *
     * 许多金融服务和医疗保健公司有一个共同需求，那就是它们必须要证明在生产中所部署的软件的可追溯性 —— 一直
     * 追溯到所有较低的环境，接着追溯到构建软件的构建作业，然后追溯到代码何时被签入到源代码存储库中。在帮助
     * 组织满足这个需求时，不可变的服务器模式确实很有亮点。正如在构建示例中所看到的那样，将使用相同的构建名
     * 称标记源代码管理存储库以及将要部署的容器镜像。这个构建的名字是独一无二的，并且与一个 Travis 构建编号
     * 联系起来。因为只是在通过每个环境时提升容器镜像，并且每个容器镜像都使用构建名称进行标记，所以已经建立
     * 了该容器镜像的可追溯性，并将其追溯至与之相关的源代码。因为容器一旦被标记就永远不会被更改，所以就拥有
     * 了强大的审查功能，以展示已部署的代码与底层的源代码存储库相匹配。现在，如果你想要更加安全，那么在为项
     * 目源代码添加标签时，还可以使用这个为构建生成的相同标签来标记驻留在 Spring Cloud Config 存储库中
     * 的应用程序配置。
     *
     *
     *
     * 3、执行构建
     *
     * 此时，所有的预构建配置和依赖项安装都已完成。要执行构建，将要使用 Travis 的 script 属性。就像
     * before_install 属性一样，script 属性也会接受一系列将被执行的命令。由于这些命令太过冗长，这
     * 里选择将构建中的每个主要步骤封装到它自己的 shell 脚本中，并让 Travis 执行 shell 脚本。如下
     * 代码展示了在构建中将要采用的主要步骤。
     *
     * script:
     *   - sh travis_scripts/tag_build.sh
     *   - sh travis_scripts/build_services.sh
     *   - sh travis_scripts/deploy_to_docker_hub.sh
     *   - sh travis_scripts/deploy_amazon_ecs.sh
     *   - sh travis_scripts/trigger_platform_tests.sh
     *
     * 下面来看一下在脚本步骤中执行的每个主要步骤。
     *
     *
     *
     * 4、标记源代码
     *
     * travis_scripts/tag_build.sh 脚本负责使用构建名称标记代码库中的代码。对于这里的示例，将通过 GitHub
     * REST API 创建一个 GitHub 发布版本。一个 GitHub 发布版本不仅会标记源代码控制库，而且还会允许开发人员
     * 将版本注释等内容连同源代码是否为代码的预发布版本一起发布到 GitHub 网页上。
     *
     * 因为 GitHub 发布 API 是一个基于 REST 的调用，所以将在 shell 脚本中使用 curl 来执行实际的调用。如下
     * 代码展示了 travis_scripts/tag_build.sh 脚本中的代码。
     *
     * echo "Tagging build with $BUILD_NAME"
     * export TARGET_URL="https://api.github.com/　　⇽---　 GitHub 发布 API 的目标端点
     * ➥  repos/carnellj/spmia-chapter10/
     * ➥  releases?access_token=$GITHUB_TOKEN"
     *
     * body="{　　⇽---　 REST调用的JSON体
     *   \"tag_name\": \"$BUILD_NAME\",
     *   \"target_commitish\": \"master\",
     *   \"name\": \"$BUILD_NAME\",
     *   \"body\": \"Release of version $BUILD_NAME\",
     *   \"draft\": true,
     *   \"prerelease\": true
     * }"
     *
     * curl –k -X POST \　　⇽---　 使用 curl 来调用用于启动构建的服务
     *    -H "Content-Type: application/json" \
     *    -d "$body" \
     *    $TARGET_URL
     *
     * 这个脚本非常简单。要做的第一件事就是为 GitHub 发布 API 构建目标 URL：
     *
     * export TARGET_URL="https://api.github.com/
     *   ➥ repos/carnellj/spmia-chapter10/
     *   ➥ releases?access_token=$GITHUB_TOKEN"
     *
     * 在 TARGET_URL 中，传递了一个名为 access_token 的 HTTP 查询参数。这个参数包含一个 GitHub 个人访问
     * 令牌，它特别被设置为允许脚本通过 REST API 执行操作。GitHub 个人访问令牌存储在名为 GITHUB_TOKEN 的
     * 加密环境变量中。要生成个人访问令牌，可登录到 GitHub 账户并导航至 https://github.com/settings
     * /tokens。在生成令牌时，要确保将令牌剪切并立即粘贴出来。当离开 GitHub 界面时该令牌就会消失，需要重新
     * 生成它。
     *
     * 脚本中的第二步是为 REST 调用创建 JSON 体：
     *
     * body="{
     *   \"tag_name\": \"$BUILD_NAME\",
     *   \"target_commitish\": \"master\",
     *   \"name\": \"$BUILD_NAME\",
     *   \"body\": \"Release of version $BUILD_NAME\",
     *   \"draft\": true,
     *   \"prerelease\": true
     * }"
     *
     * 在前面的代码片段中，提供了 $BUILD_NAME 作为 tag_name 的值，并使用 body 字段设置基本的发布版本注释。
     *
     * 一旦构建了调用的 JSON 体，通过 curl 命令执行这个调用就很简单了：
     *
     * curl –k -X POST \
     *   -H "Content-Type: application/json" \
     *   -d "$body" \
     *   $TARGET_URL
     *
     *
     *
     * 5、构建微服务并创建 Docker 镜像
     *
     * Travis 脚本属性中的下一步是构建各个服务，然后为每个服务创建 Docker 容器镜像。可以通过一个名为
     * travis_scripts/build_services.sh 的小脚本来完成这一步骤。该脚本将执行以下命令：
     *
     * mvn clean package docker:build
     *
     * 这个 Maven 命令为代码存储库中的所有服务执行父 Maven 的 pom.xml 文件。父 pom.xml 为每个服务
     * 执行单独的 Maven pom.xml，然后每个单独的服务都会构建服务源代码，执行所有单元测试和集成测试，
     * 然后将服务打包为可执行的 jar 文件。
     *
     * 在 Maven 构建中发生的最后一件事情是创建一个 Docker 容器镜像，它将被推送到在 Travis 构建机器
     * 上运行的本地 Docker 存储库。Docker 镜像的创建是使用 Spotify Docker 插件完成的。
     *
     *
     *
     * 6、将镜像推送到 Docker Hub
     *
     * 在构建的当前阶段，服务已经被编译和打包，并且在 Travis 构建机器上 Docker 容器镜像已经被创建。现在将
     * 通过 travis_scripts/deploy_to_docker_hub.sh 脚本将 Docker 容器镜像推送到中央 Docker 存储库。
     * 对于已创建的 Docker 镜像来说，Docker 存储库就像 Maven 存储库一样。Docker 镜像可以被标记并上传到
     * Docker 存储库中，其他项目可以下载和使用这些镜像。
     *
     * 对于这个代码示例，将使用 Docker Hub。如下代码展示了在 travis_scripts/ deploy_to_docker_hub.sh
     * 脚本中使用的命令。
     *
     * echo "Pushing service docker images to docker hub ...."
     * docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
     * docker push johncarnell/tmx-authentication-service:$BUILD_NAME
     * docker push johncarnell/tmx-licensing-service:$BUILD_NAME
     * docker push johncarnell/tmx-organization-service:$BUILD_NAME
     * docker push johncarnell/tmx-confsvr:$BUILD_NAME
     * docker push johncarnell/tmx-eurekasvr:$BUILD_NAME
     * docker push johncarnell/tmx-zuulsvr:$BUILD_NAME
     *
     * 这个 shell 脚本的流程很简单。这里要做的第一件事就是使用 Docker 命令行工具和 Docker Hub 账户的用
     * 户凭据登录到 Docker Hub，镜像将被推送到这个 Docker Hub。记住，用于 Docker Hub 的凭据以加密环境
     * 变量的方式进行存储。
     *
     * docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
     *
     * 脚本登录后，代码会将各个微服务的 Docker 镜像推送到 Docker Hub 存储库，目前这些 Docker 镜像驻留在
     * Travis 构建服务器上运行的本地 Docker 存储库中。
     *
     * docker push johncarnell/tmx-confsvr:$BUILD_NAME
     *
     * 上述命令告诉 Docker 命令行工具，将 Docker Hub（这是 Docker 命令行工具使用的默认 Hub）推送到
     * johncarnell 账户下。正在推送的镜像是 tmx-confsvr 镜像，其标记名称是 $BUILD_NAME 环境变量的
     * 值。
     *
     *
     *
     * 7、在 Amazon ECS 中启动服务
     *
     * 到目前为止，所有的代码都已经被构建和标记，并且已经创建了一个 Docker 镜像。现在已准备好将服务部署到
     * 之前创建的 Amazon ECS 容器。完成这项部署所做的工作可在 travis_scripts/deploy_to_amazon_ecs.sh
     * 中找到。如下代码展示了这个脚本的代码。
     *
     * echo "Launching $BUILD_NAME IN AMAZON ECS"
     * ecs-cli configure --region us-west-1 \
     *                   --access-key $AWS_ACCESS_KEY
     *                   --secret-key $AWS_SECRET_KEY
     *                   --cluster spmia-tmx-dev
     * ecs-cli compose --file docker/common/docker-compose.yml up
     * rm –rf ~/.ecs
     *
     * 注意：在 AWS 控制台中，仅显示该地区所在的州/城市/国家的名称，而不是实际的地区名称（如 us-west-1、
     * us-east-1 等）。例如，如果读者查看 AWS 控制台，并希望看到北加利福尼亚地区，则没有迹象表明，该地
     * 区的名称是 us-west-1。
     *
     * 由于 Travis 在每次构建时都会启动新的构建虚拟机，所以需要使用 AWS 访问密钥和私密密钥来配置构建环境
     * 的 ecs-cli 客户端。完成之后，可以使用 ecs-cli compose 命令和 docker-compose.yml 文件启动到
     * ECS 集群的部署。docker-compose.yml 通过参数化的方式使用构建名称（包含在环境变量 $BUILD_NAME 中）。
     *
     *
     *
     * 8、启动平台测试
     *
     * 构建过程还有最后一步 —— 启动平台测试。在每次部署到新环境之后，都要启动一系列平台测试，以确保所有服务
     * 都正常工作。平台测试的目标是在已部署的构建中调用微服务，并确保服务正常工作。
     *
     * 这里将平台测试作业与主构建分离，以便平台测试可以独立于主构建被调用。为此，这里使用 Travis CI REST
     * API 以编程方式调用平台测试。travis_scripts/trigger_platform_tests.sh 脚本负责完成这项工作。
     * 如下代码展示了这个脚本的代码。
     *
     * echo "Beginning platform tests for build $BUILD_NAME"
     * travis login --org --no-interactive \
     *              --github-token $GITHUB_TOKEN　　⇽---　 使用 GitHub 令牌通过 Travis CI 登录，
     *                                                    将返回的令牌存储在 RESULTS 变量中
     * export RESULTS=`travis token --org`
     * export TARGET_URL="https://api.travis-ci.org/repo/
     *      carnellj%2F$PLATFORM_TEST_NAME/requests"
     * echo "Kicking off job using target url: $TARGET_URL"
     *
     * body="{
     * \"request\": {
     *   \"message\": \"Initiating platform tests for build $BUILD_NAME\",
     *   \"branch\":\"master\",
     *   \"config\": {
     *     \"env\": {
     *       \"global\": [\"BUILD_NAME=$BUILD_NAME\",　　⇽---　 构建调用的 JSON 体，将两个值传递给下游作业
     *                     \"CONTAINER_IP=$CONTAINER_IP\"]
     *      }
     *    }
     *  }}"
     *
     *  curl -s -X POST \　　⇽---　 使用 curl 调用 Travis CI REST API
     *    -H "Content-Type: application/json" \
     *    -H "Accept: application/json" \
     *    -H "Travis-API-Version: 3" \
     *    -H "Authorization: token $RESULTS" \
     *    -d "$body" \
     *    $TARGET_URL
     *
     * 这段代码做的第一件事是使用 Travis CI 命令行工具登录到 Travis CI 并获得一个可用于调用其他 Travis
     * REST API 的 OAuth2 令牌。这里将此 OAuth2 令牌存储在 $RESULTS 环境变量中。
     *
     * 接下来，为 REST API 调用构建 JSON 体。下游 Travis CI 作业启动了一系列测试 API 的 Python 脚本。
     * 这个下游作业期望设置两个环境变量。在这段代码中构建的 JSON 体中，传递了两个环境变量，即 $BUILD_NAME
     * 和 $CONTAINER_IP，这些变量将被传递给测试作业：
     *
     * \"env\": {
     *   \"global\": [\"BUILD_NAME=$BUILD_NAME\",
     *                \"CONTAINER_IP=$CONTAINER_IP\"]
     * }
     *
     * 脚本中的最后一个操作是调用运行平台测试脚本的 Travis CI 构建作业。这是通过使用 curl 命令为测试作业
     * 调用 Travis CI REST 端点来完成的：
     *
     * curl -s -X POST \
     *   -H "Content-Type: application/json" \
     *   -H "Accept: application/json" \
     *   -H "Travis-API-Version: 3" \
     *   -H "Authorization: token $RESULTS" \
     *   -d "$body" \
     *   $TARGET_URL
     *
     * 这段平台测试脚本被单独存储在 chapter10th-demo2nd 中。这个存储库有三个 Python 脚本，它们用于测试
     * Spring Cloud Config 服务器、Eureka 服务器和 Zuul 服务器。Zuul 服务器平台测试还测试许可证服务
     * 和组织服务。就测试服务的各个方面来说，这些测试并不全面，但是它们确实对服务执行了足够多的测试，以确保
     * 服务能够正常工作。
     */
    public static void main(String[] args) {

    }

}
