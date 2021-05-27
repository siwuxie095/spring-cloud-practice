package com.siwuxie095.spring.cloud.chapter3th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-05-27 21:42:16
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 建立 Spring Cloud Config Server
     *
     * Spring Cloud 配置服务器是构建在 Spring Boot 之上的基于 REST 的应用程序。它不是一个独立的服务器。
     * 相反，你可以选择将其嵌入到现有的 Spring Boot 应用程序中，或者使用服务器启动一个新的 Spring Boot
     * 项目的时候嵌入它。
     *
     * 你需要做的第一件事是建立一个新的项目，目录称为 config-server。在 config-server 目录你会创建一个
     * 新的 Maven 文件，它将用来拉下启动你的 Spring Cloud 配置服务器需要的 JARs。
     *
     * Spring Cloud 是大量独立项目的集合，它们都随自己的版本移动。这个父 BOM 包含了云项目中使用的所有第
     * 三方库和依赖项，以及组成该版本的各个项目的版本号。在这个例子中，你使用版本 Camden.SR5 的 Spring
     * Cloud。利用 BOM 的定义，你能保证在 Spring Cloud 项目中使用兼容版本的子项目。它还意味着你不必为
     * 子依赖项声明版本号。
     *
     * 这里的示例涉及声明你将在服务中使用的特定 Spring Cloud 依赖项。如下：
     *
     *         <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-starter-config</artifactId>
     *         </dependency>
     *
     *         <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-config-server</artifactId>
     *         </dependency>
     *
     * 第一个依赖项是所有 Spring Cloud 项目使用的 spring-cloud-starter-config 依赖项。第二个依赖项是
     * spring-cloud-config-server 启动项目。它包含了 spring-cloud-config-server 的核心库。
     *
     *
     * PS：来吧，坐列车，发布列车
     *
     * Spring Cloud 使用非传统机制标记 Maven 项目。Spring Cloud 是一个独立的子项目集。Spring Cloud
     * 团队通过所谓的 "发布列车" 发布他们的版本。所有的子项目组成 Spring Cloud，它们使用 Maven 文件
     * （BOM）打包并作为一个整体发布。Spring Cloud 团队已经利用伦敦地铁站的名字作为他们发布的名称，每个
     * 增量主要发布给伦敦地铁站，有下一个最高字母时停止。已经有三个版本：Angel、Brixton 和 Camden。
     * Camden 是迄今为止最新的版本，但在子项目的分支内仍有多个候选版本。
     *
     * 需要注意的一点是，Spring Boot 是独立于 Spring Cloud 发布列车发布的。因此，不同版本的 Spring
     * Boot 与 Spring Cloud 的不同版本是不兼容的。你可以看到 Spring Boot 和 Spring Cloud 之间的
     * 版本依赖关系，以及包含在发布培训项目的不同子项目版本，请参照 Spring Cloud 网站：
     * https://spring.io/projects/spring-cloud
     *
     *
     * 你仍然需要设置一个文件以获取核心配置服务器并运行。这个文件是 application.yml， 它在 config-
     * service/src/main/resources 目录。application.yml 文件会告诉 Spring Cloud 配置服务所监听的
     * 端口和定位的后端，后端将提供配置数据。
     *
     * 你几乎准备好启动 Spring Cloud 配置服务了。你需要将服务器指向保存配置数据的后端存储库。在这里，你
     * 将使用构建的许可服务作为如何使用 Spring Cloud Config 的一个示例。为了使事情简单，你将为三个环境
     * 设置应用程序配置数据：本地运行服务时的默认环境、开发环境和生产环境。
     *
     * 在 Spring Cloud 配置中，一切都脱离层次结构。应用程序配置由应用程序的名称来表示，然后为每个环境设
     * 置一个属性文件，以便为其配置信息。在每个环境中，你将设置两个配置属性：
     * （1）将由你的许可服务直接使用的示例属性。
     * （2）用于存储许可服务数据的 Postgres 数据库的数据库配置。
     *
     * Spring Cloud 配置将特定环境的属性暴露为基于 HTTP 的端点。有一点要注意的是，当你建立你的配置服务，
     * 这将是另一个运行在你的环境中的微服务。一旦设置完毕，服务的内容就可以通过基于 HTTP 的 REST 端点访
     * 问。即 Spring Cloud 配置服务器会作为微服务运行和暴露。
     *
     * 应用程序配置文件命名约定为 appName-env.yml。这里的环境名称直接转换为将访问配置信息的 URL。然后，
     * 当你启动许可微服务示例时，你想运行服务的环境，通过你进入命令行服务启动的 Spring Boot 概要文件指
     * 定。如果概要文件在命令行中没有指定，Spring Boot 会默认使用打包在应用程序内的 application.yml
     * 文件包含的配置数据。
     *
     * 下面是你将为许可服务提供的一些应用程序配置数据的一个示例。这些数据将包含在 licensing-service.yml
     * 文件，如下是这个文件的一部分内容：
     *
     * tracer.property: "I AM THE DEFAULT"
     * spring.jpa.database: "POSTGRESQL"
     * spring.datasource.platform: "postgres"
     * spring.jpa.show-sql: "true"
     * spring.database.driverClassName: "org.postgresql.Driver"
     * spring.datasource.url: "jdbc:postgresql://database:5432/eagle_eye_local"
     * spring.datasource.username: "postgres"
     * spring.datasource.password: "p0stgr@s"
     * spring.datasource.testWhileIdle: "true"
     * spring.datasource.validationQuery: "SELECT 1"
     * spring.jpa.properties.hibernate.dialect: "org.hibernate.dialect.PostgreSQLDialect"
     *
     *
     * PS：在实现之前先想一想
     *
     * 这里建议不要使用基于文件系统的解决方案来解决中型到大型云应用程序。使用文件系统方法意味着需要为希望
     * 访问应用程序配置数据的所有云配置服务器实现共享文件挂载点。在云中设置共享文件系统服务器是可行的，但
     * 它将维护此环境的责任放在了你身上。
     *
     * 这里展示了文件系统方法，当使用 Spring Cloud 配置服务器时，它是最容易使用的示例。后续将展示如何配
     * 置 Spring Cloud 配置服务器使用基于云的 Git 提供商，如 Bitbucket 或 GitHub 存储应用程序配置。
     *
     *
     *
     * 1、创建 Spring Cloud 配置引导类
     *
     * 这里所涵盖的每一个 Spring Cloud 服务都需要一个引导类来启动服务。这个引导类将包含两点：一个 Java
     * main() 方法，作为服务启动的切入点，和一套 Spring Cloud 注解，它们告诉正在启动的服务，它们将为服
     * 务提供什么样的 Spring Cloud 行为。
     *
     * 下面显示的 src/main/java/com/siwuxie095/spring/cloud/confsvr/ConfigServerApplication
     * .java 类，被用作配置服务的引导类。
     *
     * @SpringBootApplication
     * @EnableConfigServer
     * public class ConfigServerApplication {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(ConfigServerApplication.class, args);
     *     }
     *
     * }
     *
     * 接下来，将用最简单的示例建立 Spring Cloud 配置服务器：文件系统。
     *
     *
     *
     * 2、通过配置文件使用 Spring Cloud Config Server
     *
     * Spring Cloud 配置服务器使用在 src/main/resources/application.yml 文件的一个指向仓库的入口
     * 点，仓库将存储应用程序的配置数据。建立基于文件系统的存储库是实现这一目标的最简单方法。
     *
     * 为此，添加以下信息来配置服务器的 application.yml 文件。下面的列表显示了 Spring Cloud 配置服务
     * 器的 application.yml 文件的内容。
     *
     * server:
     *   port: 8888
     *
     * spring:
     *   profiles:
     *     active: native
     *   cloud:
     *     config:
     *       server:
     *         native:
     *           searchLocations: file:///Users/siwuxie095/book/
     *           native_cloud_apps/chapter3rd-demo1st/config-service/src/main/
     *           resources/config/licensingservice
     *
     * 在清单中的配置文件中，你首先告诉配置服务器它应该为所有配置请求监听哪个端口号：
     *
     * server:
     *   port: 8888
     *
     * 因为你正在使用文件系统来存储应用程序配置信息，所以你需要告诉 Spring Cloud 配置服务器以 "native"
     * 配置文件运行：
     *
     *   profiles:
     *     active: native
     *
     * 在 application.yml 文件最后一部分，Spring Cloud 配置提供了应用数据所在的目录：
     *
     *     config:
     *       server:
     *         native:
     *           searchLocations: file:///Users/siwuxie095/book/
     *           native_cloud_apps/chapter3rd-demo1st/config-service/src/main/
     *           resources/config/licensingservice
     *
     * 配置项的重要参数是 searchLocations 属性。这个属性为每个应用程序提供一个逗号分隔的目录列表，每个
     * 应用程序都有由配置服务器管理的属性。在这个示例中，你只配置了许可服务。
     *
     * 注意：如果你使用 Spring Cloud 配置本地文件系统的版本，你需要修改 spring.cloud.config.server
     * .native.searchlocations 属性来反映当在本地运行你的代码时你的本地文件路径。
     *
     * 你现在已经完成了足够的工作来启动配置服务器。继续使用 mvn spring-boot:run 命令启动配置服务器。在
     * 命令行上，服务器现在应该使用 Spring Boot 启动画面启动。如果你将你的浏览器指向到
     * http://localhost:8888/licensingservice/default，你会看到 JSON 负载返回所有包含在
     * licensingservice.yml 文件中的属性。
     *
     * 如果你想看到许可服务开发环境的配置信息，点击 http://localhost:8888/licensingservice/dev 端
     * 点，以 GET 方式获取。
     *
     * 如果仔细查看，你会发现，当你点击 dev 端点时，将返回许可服务的默认配置属性和许可服务的开发环境配置。
     * Spring Cloud 配置返回两组配置信息的原因是 Spring 框架实现了一个分层的解析属性机制。当 Spring
     * 框架执行属性解析时，它总是首先查找默认属性中的属性。然后，如果存在一个特定于环境的值，则覆盖默认值。
     *
     * PS：如果只访问 http://localhost:8888/licensingservice-dev/default，就只有开发环境配置。
     *
     * 当你请求一个特定环境的概要文件时，概要文件和默认配置文件都返回。
     *
     * 具体而言，如果你在 licensingservice.yml 文件定义一个属性，且不能确定它在任何其他环境的配置文件
     * （例如，在 licensingservice-dev.yml），Spring 框架将使用默认值。
     *
     * 注意：这不是你通过直接调用 Spring Cloud 配置 REST 端点所看到的行为。REST 端点被调用时，将返回
     * 所有配置的默认值和这些配置特定于环境的值。
     *
     * 后续将会介绍如何将 Spring Cloud 配置服务器集成到你的许可微服务。
     */
    public static void main(String[] args) {

    }

}
