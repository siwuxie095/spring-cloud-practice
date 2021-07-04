package com.siwuxie095.spring.cloud.chapter3th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-05-27 21:42:16
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 构建 Spring Cloud 配置服务器
     *
     * Spring Cloud 配置服务器是基于 REST 的应用程序，它建立在 Spring Boot 之上。Spring Cloud 配置服务器
     * 不是独立服务器，相反，开发人员可以选择将它嵌入现有的 Spring Boot 应用程序中，也可以在嵌入它的服务器中
     * 启动新的 Spring Boot 项目。
     *
     * 首先需要做的是建立一个名为 config-server 的新项目目录。在 config-server 目录中创建一个新的 Maven
     * 文件，该文件将用于拉取启动 Spring Cloud 配置服务器所需的 JAR 文件。如下代码列出的是关键部分，而不是
     * 整个 Maven 文件。
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <project xmlns="http://maven.apache.org/POM/4.0.0"
     *     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://
     *     ➥  maven.apache.org/xsd/maven-4.0.0.xsd">
     *   <modelVersion>4.0.0</modelVersion>
     *
     *   <groupId>com.thoughtmechanix</groupId>
     *   <artifactId>configurationserver</artifactId>
     *   <version>0.0.1-SNAPSHOT</version>
     *   <packaging>jar</packaging>
     *
     *   <name>Config Server</name>
     *   <description>Config Server demo project</description>
     *
     *   <parent>
     *     <groupId>org.springframework.boot</groupId>
     *     <artifactId>spring-boot-starter-parent</artifactId>
     *     <version>1.4.4.RELEASE</version>    ⇽---  将要使用的 Spring Boot 版本
     *   </parent>
     *   <dependencyManagement>
     *     <dependencies>
     *       <dependency>
     *         <groupId>org.springframework.cloud</groupId>
     *         <artifactId>spring-cloud-dependencies</artifactId>
     *         <version>Camden.SR5</version>    ⇽---  将要使用的 Spring Cloud 版本
     *         <type>pom</type>
     *         <scope>import</scope>
     *       </dependency>
     *     </dependencies>
     *   </dependencyManagement>
     *
     *   <properties>
     *     <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
     *     <start-class>com.thoughtmechanix.confsvr.
     *     ➥  ConfigServerApplication</start-class>    ⇽---  配置服务器将要使用的引导类
     *     <java.version>1.8</java.version>
     *     <docker.image.name>johncarnell/tmx-confsvr</docker.image.name>
     *     <docker.image.tag>chapter3</docker.image.tag>
     *   </properties>
     *
     *   <dependencies>
     *     <dependency>
     *       <groupId>org.springframework.cloud</groupId>    ⇽---  在这个特定服务中将要使用的 Spring Cloud 项目
     *       <artifactId>spring-cloud-starter-config</artifactId>
     *     </dependency>
     *
     *     <dependency>
     *       <groupId>org.springframework.cloud</groupId>    ⇽---在这个特定服务中将要使用的 Spring Cloud 项目
     *       <artifactId>spring-cloud-config-server</artifactId>
     *     </dependency>
     *   </dependencies>
     *
     * <!-- 未显示Docker构建配置 -->
     * </project>
     *
     * 在这段代码所示的 Maven 文件中，首先声明了要用于微服务的 Spring Boot 版本（1.4.4 版本）。下一个重要的
     * Maven 定义部分是将要使用的 Spring Cloud Config 父物料清单（Bill of Materials，BOM）。
     *
     * Spring Cloud 是一个大量独立项目的集合，这些项目全部遵循自身的发行版本而更新。此父 BOM 包含云项目中使用
     * 的所有第三方库和依赖项以及构成该版本的各个项目的版本号。在这个例子中，使用 Spring Cloud 的 Camden.SR5
     * 版本。通过使用 BOM 定义，可以保证在 Spring Cloud 中使用子项目的兼容版本。这也意味着不必为子依赖项声明
     * 版本号。这段代码的剩余部分负责声明将在服务中使用的特定 Spring Cloud 依赖项。第一个依赖项是所有 Spring
     * Cloud 项目使用的 spring-cloud-starter-config。第二个依赖项是 spring-cloud-config-server 起步项
     * 目，它包含了 spring-cloud-config-server 的核心库。
     *
     *
     * PS：来吧，坐上发行版系列的列车
     *
     * Spring Cloud 使用非传统机制来标记 Maven 项目。Spring Cloud 是独立子项目的集合。Spring Cloud 团队
     * 通过其称为 "发行版系列"（release train）的方式进行版本发布。组成 Spring Cloud 的所有子项目都包含在
     * 一个 Maven 物料清单（BOM）中，并作为一个整体进行发布。Spring Cloud 团队一直使用伦敦地铁站的名称作为
     * 他们发行版本的名称，每个递增的主要版本都按字母表从小到大的顺序赋予一个伦敦地铁站的站名。目前已有三个版本，
     * 即 Angel、Brixton 和 Camden。Camden 是迄今为止最新的发行版，但是它的子项目中仍然有多个候选版本分支。
     *
     * 需要注意的是，Spring Boot 是独立于 Spring Cloud 发行版系列发布的。因此，Spring Boot 的不同版本可能
     * 与 Spring Cloud 的不同版本不兼容。参考 Spring Cloud 网站，可以看到 Spring Boot 和 Spring Cloud
     * 之间的版本依赖项，以及发行版系列中包含的不同子项目版本。
     *
     *
     * 这里仍然需要再多创建一个文件来让核心配置服务器正常运行。这个文件是位于 resources 目录中的 application
     * .yml 文件。application.yml 文件告诉 Spring Cloud 配置服务要侦听哪个端口以及在哪里可以找到提供配置
     * 数据的后端。
     *
     * 马上就能启动 Spring Cloud 配置服务了。现在，需要将服务器指向保存配置数据的后端存储库。这里将要使用之前
     * 构建的许可证服务作为使用 Spring Cloud Config 的示例。简单起见，这里将为以下三个环境创建配置数据：在本
     * 地运行服务时的默认环境、开发环境以及生产环境。
     *
     * 在 Spring Cloud 配置中，一切都是按照层次结构进行的。应用程序配置由应用程序的名称表示。这里为需要拥有配
     * 置信息的每个环境提供一个属性文件。在这些环境中，将创建两个配置属性：
     * （1）由许可证服务直接使用的示例属性；
     * （2）用于存储许可证服务数据的 Postgres 数据库的配置。
     *
     * 需要注意的是，在构建配置服务时，它将成为在环境中运行的另一个微服务。一旦建立配置服务，服务的内容就可以通
     * 过基于 HTTP 的 REST 端点进行访问。
     *
     * 应用程序配置文件的命名约定是 "应用程序名称-环境名称.yml"。环境名称直接转换为可以浏览配置信息的 URL。随
     * 后，启动许可证微服务示例时，要运行哪个服务环境是由在命令行服务启动时传入的 Spring Boot 的 profile 指
     * 定的。如果在命令行上没有传入 profile，Spring Boot 将始终默认加载随应用程序打包的 application.yml
     * 文件中的配置数据。
     *
     * 以下是为许可证服务提供的一些应用程序配置数据的示例。这些数据包含在 resources/config/licensingservice
     * /licensingservice.yml 文件中。下面是此文件的一部分内容：
     *
     * tracer.property: "I AM THE DEFAULT"
     * spring.jpa.database: "POSTGRESQL"
     * spring.datasource.platform:  "postgres"
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
     * PS：在实施前想一想
     *
     * 这里建议不要在中大型云应用中使用基于文件系统的解决方案。使用文件系统方法，意味着要为想要访问应用程序配置
     * 数据的所有云配置服务器实现共享文件挂载点。在云中创建共享文件系统服务器是可行的，但它将维护此环境的责任放
     * 在开发人员身上。
     *
     * 这里将展示如何以文件系统作为入门使用 Spring Cloud 配置服务器的最简单示例。后续将介绍如何配置 Spring
     * Cloud 配置服务器以使用基于云的 Git 供应商（如 Bitbucket 或 GitHub）来存储应用程序配置。
     *
     *
     *
     * 1、创建 Spring Cloud Config 引导类
     *
     * 这里涵盖的每一个 Spring Cloud 服务都需要一个用于启动该服务的引导类。这个引导类包含两样东西：作为服务
     * 启动入口点的 Java main() 方法，以及一组告诉启动的服务将要启动哪种类型的 Spring Cloud 行为的 Spring
     * Cloud 注解。
     *
     * 如下代码展示了用作配置服务的引导类 Application。
     *
     * @SuppressWarnings("all")
     * // Spring Cloud Config 服务是 Spring Boot 应用程序，因此需要用 @SpringBootApplication 进行标记
     * @SpringBootApplication
     * // @EnableConfigServer 使服务成为 Spring Cloud Config 服务
     * @EnableConfigServer
     * public class ConfigServerApplication {
     *
     *     // main 方法启动服务并启动 Spring 容器
     *     public static void main(String[] args) {
     *         SpringApplication.run(ConfigServerApplication.class, args);
     *     }
     *
     * }
     *
     * 接下来，将使用最简单的文件系统示例来搭建 Spring Cloud 配置服务器。
     *
     *
     *
     * 2、使用带有文件系统的 Spring Cloud 配置服务器
     *
     * Spring Cloud 配置服务器使用 application.yml 文件中的条目指向要保存应用程序配置数据的存储库。创建基
     * 于文件系统的存储库是实现这一目标的最简单方法。
     *
     * 为此，要将以下信息添加到配置服务器的 application.yml 文件中。如下代码展示 Spring Cloud 配置服务器
     * 的 application.yml 文件的内容。
     *
     * server:
     *   port: 8888    ⇽---  Spring Cloud 配置服务器将要监听的端口
     * spring:
     *   profiles:
     *     active: native    ⇽---  用于存储配置的后端存储库（文件系统）
     *   cloud:
     *     config:
     *       server:
     *         native:
     *           searchLocations: file:///Users/johncarnell1/book/spmia-code
     *           /chapter3-code/confsvr/src/main/resources/config/
     *            licensingservice    ⇽---  配置文件存储位置的路径
     *
     * 在这段代码所示的配置文件中，首先告诉配置服务器，对于所有配置信息的请求，应该监听哪个端口号：
     *
     * server:
     *   port: 8888
     *
     * 因为这里正在使用文件系统来存储应用程序配置信息，所以需要告诉 Spring Cloud 配置服务器以 "native"
     * profile 运行：
     *
     * profiles:
     *   active: native
     *
     * application.yml 文件的最后一部分为 Spring Cloud 配置提供了应用程序数据所在的文件目录：
     *
     * server:
     *   native:
     *     searchLocations: file:///Users/johncarnell1/book/spmia_code
     *     /chapter3-code/confsvr/src/main/resources/config/licensingservice
     *
     * 配置条目中的重要参数是 searchLocations 属性。这个属性为每一个应用程序提供了用逗号分隔的文件夹列表，
     * 这些文件夹含有由配置服务器管理的属性。在上一个示例中，只配置了许可证服务。
     *
     * 注意：如果使用 Spring Cloud Config 的本地文件系统版本，那么在本地运行代码时，需要修改 spring.cloud
     * .config.server.native.searchLocations 属性以反映本地文件路径。
     *
     * 现在已经完成了足够多的工作来启动配置服务器。接下来，就使用 mvn spring-boot:run 命令启动配置服务器。
     * 服务器现在应该在命令行上出现一个 Spring Boot 启动画面。如果用浏览器访问 http://localhost:8888
     * /licensingservice/default，那么将会看到 JSON 净荷与 licensingservice.yml 文件中包含的所有属
     * 性一起返回。
     *
     * 如果想要查看基于开发环境的许可证服务的配置信息，可以对 http://localhost:8888/licensingservice
     * /dev 端点发起 GET 请求。
     *
     * 如果仔细观察，你会看到在访问开发环境端点时，将返回许可证服务的默认配置属性以及开发环境下的许可证服务
     * 配置。Spring Cloud 配置返回两组配置信息的原因是，Spring 框架实现了一种用于解析属性的层次结构机制。
     * 当 Spring框架执行属性解析时，它将始终先查找默认属性中的属性，然后用特定环境的值（如果存在）去覆盖
     * 默认属性。
     *
     * PS：如果只访问 http://localhost:8888/licensingservice-dev/default，就只有开发环境配置。
     *
     * 具体来说，如果在 licensingservice.yml 文件中定义一个属性，并且不在任何其他环境配置文件
     * （如 licensingservice-dev.yml）中定义它，则 Spring 框架将使用这个默认值。
     *
     * 注意：这不是直接调用 Spring Cloud 配置 REST 端点所看到的行为。REST 端点将返回调用的默认值和环境
     * 特定值的所有配置值。
     *
     * 后续会介绍如何将 Spring Cloud 配置服务器挂钩到许可证微服务。
     */
    public static void main(String[] args) {

    }

}
