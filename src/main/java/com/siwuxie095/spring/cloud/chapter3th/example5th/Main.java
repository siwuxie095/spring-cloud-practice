package com.siwuxie095.spring.cloud.chapter3th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-05-29 21:18:47
 */
public class Main {

    /**
     * 保护敏感配置信息
     *
     * 默认情况下，Spring Cloud 配置服务器在应用程序的配置文件中以纯文本格式存储所有属性。这包括诸如
     * 数据库凭据之类的敏感信息。
     *
     * 将敏感凭据存储为源代码存储库中的纯文本是极其糟糕的做法。不幸的是，这种情况比你想象的要频繁得多。
     * Spring Cloud Config 使你能够轻松地加密敏感属性。Spring Cloud Config 支持使用对称（共享密
     * 钥）和非对称加密（公钥/私钥）。
     *
     * 这里将了解如何设置 Spring Cloud 配置服务器，以便使用对称密钥进行加密。要做到这一点，你需要：
     * （1）下载并安装加密所需的 Oracle JCE JAR 包。
     * （2）设置加密密钥。
     * （3）加密和解密属性。
     * （4）配置微服务使用加密的客户端。
     *
     *
     *
     * 1、下载和安装加密所需的 Oracle JCE JAR 包
     *
     * 首先，你需要下载并安装 Oracle 的无长度限制的 Java 加密扩展（JCE）。这不能通过 Maven 下载，
     * 必须通过 Oracle 公司下载。一旦你下载了 包含 JCE jars 的 ZIP 文件，你必须做到以下几点：
     * （1）找到你的 $JAVA_HOME/jre/lib/security 目录。
     * （2）备份在 $JAVA_HOME/jre/lib/security 目录的 local_policy.jar 和 US_export_policy
     * .jar 文件到另一个位置。
     * （3）解压从 Oracle 下载的 JCE zip 文件。
     * （4）复制 local_policy.jar 和 US_export_policy.jar 到 $JAVA_HOME/jre/lib/security
     * 目录。
     * （5）配置 Spring Cloud Config 以使用加密。
     *
     * Oracle JCE 下载链接如下：
     * https://www.oracle.com/java/technologies/javase-jce8-downloads.html
     *
     *
     *
     * 2、配置加密密钥
     *
     * 一旦 JAR 文件就位，你就需要设置一个对称加密密钥。对称加密密钥无非是一个共享的密钥，被用作加密者
     * 来加密值和用作解密者来解密值。在 Spring Cloud 配置服务器，对称加密密钥是一个你选择的字符串，
     * 它通过操作系统环境变量 ENCRYPT_KEY 传递给服务。
     *
     * 为了这里的目的，你将总是设置环境变量 ENCRYPT_KEY 为：export ENCRYPT_KEY=IMSYMMETRIC
     *
     * 注意两个关于对称密钥的问题：
     * （1）你的对称密钥应该是 12 个或更大长度的字符，最好是一组随机字符。
     * （2）不要丢失对称密钥。否则一旦你已经使用加密密钥加密一些东西，你就不能再解密它。
     *
     *
     * PS：加密密钥管理
     *
     * 为了这里的目的，这里做了两件通常不会推荐在生产中部署的事情：
     * （1）这里将加密密钥设置为一个短语。因为想保持密钥简单，这样就能记住它，而且很适合作为文本阅读。
     * 在真实的部署中，将为部署的每个环境使用一个单独的加密密钥，并使用随机字符作为密钥。
     * （2）在这里，直接在 Docker 文件中硬编码 ENCRYPT_KEY 环境变量。这样做是为了让你可以下载这些
     * 文件并在不需要记住设置环境变量的情况下启动这些文件。在实际的运行环境，会在 Dockerfile 里面引
     * 用 ENCRYPT_KEY 作为操作系统环境变量。要知道不要在你的 Dockerfiles 里面硬编码你的加密密钥。
     * 记住，你的 Dockerfiles 应该在源代码下控制。
     *
     *
     *
     * 3、加密和解密属性
     *
     * 现在可以开始加密属性以便在 Spring Cloud Config 中使用了。你会加密许可服务 Postgres 数据库
     * 密码，你已经使用它访问 EagleEye 数据。这个属性称为 spring.datasource.password，目前设置
     * 为纯文本的值 p0stgr@s。
     *
     * 当你启动 Spring Cloud Config 实例，Spring Cloud Config 检测到 ENCRYPT_KEY 环境变量并
     * 自动添加了两个新的端点（/encrypt 和 /decrypt）到 Spring Cloud 配置服务。你将使用 /encrypt
     * 端点加密 p0stgr@s 值。
     *
     * 请注意，无论什么时候调用 /encrypt 或 /decrypt 端点，都需要确保你对这些端点执行了一个 POST
     * 请求。
     *
     * 如果你想解密该值，你将使用 /decrypt 端点，在调用中传入加密字符串。
     *
     * 现在你可以在许可服务使用以下语法，添加加密属性到你的 GitHub 或基于文件系统的配置文件：
     * spring.datasource.password:"{cipher}
     * 858201e10fe3c9513e1d28b33ff417a66e8c8411dcff3077c53cf53d8a1be360"
     *
     * Spring Cloud 配置服务器要求所有的加密属性都使用 {cipher} 前缀。{cipher} 值告诉 Spring
     * Cloud 配置服务器，它将处理一个加密值。启动 Spring Cloud 配置服务器并以 GET 方式点击
     * http://localhost:8888/licensing-service/default 端点。
     *
     * 你已经通过使用加密属性使 spring.datasource.password 更安全，但你仍然有一个问题。当你点击
     * http://localhost:8888/licensing-service/default 端点时，数据库密码会以纯文本暴露。
     *
     * PS：尽管 spring.datasource.password 在属性文件中被加密，但当许可服务检索配置时它会被解密。
     * 这仍然是个问题。
     *
     * 默认情况下，Spring Cloud Config 将所有在服务器上的属性解密并把结果作为明文（未加密的文本）
     * 返回给应用程序消费属性。但是，你可以告诉 Spring Cloud Config 在服务器上不进行解密，使应用
     * 程序检索配置数据并解密加密属性由应用程序负责。
     *
     *
     *
     * 4、配置微服务使用加密的客户端
     *
     * 要启用客户端对属性的解密，你需要做三件事：
     * （1）配置 Spring Cloud Config 在服务器端不解密属性。
     * （2）设置许可服务器的对称密钥。
     * （3）在许可服务的 pom.xml 文件中添加与 spring-security-rsa 相关的 JAR 包。
     *
     * 你需要做的第一件事是禁用服务器端在 Spring Cloud Config 中解密的属性。通过配置 Spring
     * Cloud Config 的 src/main/resources/application.yml 文件来设置属性 spring.cloud
     * .config.server.encrypt.enabled: false。这就是你在 Spring Cloud 配置服务器上必须
     * 做的事情。
     *
     * 因为许可服务现在负责解密加密的属性，你首先需要在许可服务设置对称密钥，确保 ENCRYPT_KEY
     * 环境变量被设置为具有相同的对称密钥(例如：IMSYMMETRIC)，对称密钥与你在 Spring Cloud
     * 服务器上使用的相同。
     *
     * 接下来，你需要在许可服务中包含 spring-security-rsa JAR 依赖项：
     *
     * <dependency>
     *     <groupId>org.springframework.security</groupId>
     *     <artifactId>spring-security-rsa</artifactId>
     * </dependency>
     *
     * 这些 JAR 文件包含解密从 Spring Cloud Config 取回的加密属性所需的 Spring 代码。有了这些
     * 更改，你就可以启动 Spring Cloud Config 和许可服务了。如果你点击http://localhost:8888
     * /licensing-service/default 端点，你将看到 spring.datasource.password 以加密形式返回。
     * 只有当调用服务从 Spring Cloud Config 加载其属性时，该属性才将被解密。
     */
    public static void main(String[] args) {

    }

}
