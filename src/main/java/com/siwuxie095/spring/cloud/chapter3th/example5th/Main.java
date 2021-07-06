package com.siwuxie095.spring.cloud.chapter3th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-05-29 21:18:47
 */
public class Main {

    /**
     * 保护敏感的配置信息
     *
     * 在默认情况下，Spring Cloud 配置服务器在应用程序配置文件中以纯文本格式存储所有属性，包括像数据库凭据
     * 这样的敏感信息。
     *
     * 将敏感凭据作为纯文本保存在源代码存储库中是一种非常糟糕的做法。遗憾的是，它发生的频率比想象的要高得多。
     * Spring Cloud Config 可以让开发者轻松加密敏感属性。Spring Cloud Config 支持使用对称加密（共享密
     * 钥）和非对称加密（公钥/私钥）。
     *
     * 这里将看看如何搭建 Spring Cloud 配置服务器以使用对称密钥的加密。要做到这一点，需要：
     * （1）下载并安装加密所需的 Oracle JCE jar；
     * （2）创建加密密钥；
     * （3）加密和解密属性；
     * （4）配置微服务以在客户端使用加密。
     *
     *
     *
     * 1、下载并安装加密所需的 Oracle JCE jar
     *
     * 首先，需要下载并安装 Oracle 的不限长度的 Java 加密扩展（Unlimited Strength Java Cryptography
     * Extension，JCE）。它无法通过 Maven 下载，必须从 Oracle 公司下载。下载包含 JCE jar 的 zip 文件
     * 后，必须执行以下操作。
     * （1）切换到 $JAVA_HOME/jre/lib/security 文件夹。
     * （2）将 $JAVA_HOME/jre/lib/security 目录中的 local_policy.jar 和 US_export_policy.jar 文
     * 件备份到其他位置。
     * （3）解压从 Oracle 下载的 JCE zip 文件。
     * （4）将 local_policy.jar 和 US_export_policy.jar 复制到 $JAVA_HOME/jre/lib/security 目录
     * 中。
     * （5）配置 Spring Cloud Config 以使用加密。
     *
     * Oracle JCE 下载链接如下：
     * https://www.oracle.com/java/technologies/javase-jce8-downloads.html
     *
     *
     * PS：自动化安装 Oracle JCE 文件的过程
     *
     * 这里已经完成了在笔记本电脑上安装 JCE 所需的手动步骤。因为这里使用 Docker 将所有的服务构建为 Docker
     * 容器，所以这里已经在 Spring Cloud Config Docker 容器中编写了这些 JAR 文件的下载和安装的脚本。下
     * 面的 OS X shell 脚本代码段展示了如何使用 curl 命令行工具进行自动化操作：
     *
     * cd /tmp/
     * curl –k-LO "http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip"
     *      -H 'Cookie: oraclelicense=accept-securebackup-cookie' && unzip
     *      jce_policy-8.zip
     * rm jce_policy-8.zip
     * yes |cp -v /tmp/UnlimitedJCEPolicyJDK8/­*.jar /usr/lib/jvm/java-1.8-openjdk/jre/
     *      lib/security/
     *
     * 这里不会去讲所有的细节，但基本上使用 CURL 下载了 JCE zip 文件（注意通过 curl 命令中的 -H 属性传递
     * 的 Cookie 头参数），然后解压文件并将其复制到 Docker 容器中的 /usr/lib/jvm/java-1.8-openjdk/jre
     * /lib/security 目录。
     *
     *
     *
     * 2、创建加密密钥
     *
     * 一旦 JAR 文件就位，就需要设置一个对称加密密钥。对称加密密钥只不过是加密器用来加密值和解密器用来解密值
     * 的共享密钥。使用 Spring Cloud 配置服务器，对称加密密钥是通过操作系统环境变量 ENCRYPT_KEY 传递给服
     * 务的字符串。在这里，需要始终将 ENCRYPT_KEY 环境变量设置为：
     *
     * export ENCRYPT_KEY=IMSYMMETRIC
     *
     * 关于对称密钥，要注意以下两点。
     * （1）对称密钥的长度应该是 12 个或更多个字符，最好是一个随机的字符集。
     * （2）不要丢失对称密钥。一旦使用加密密钥加密某些东西，如果没有对称密钥就无法解密。
     *
     *
     * PS：管理加密密钥
     *
     * 为了介绍的清晰明了，这里做了两件在生产部署中通常不会推荐的事情。
     * （1）将加密密钥设置为一句话。因为想保持密钥简单，以便能记住它，并且它能很好地进行阅读。在真实的部署中，
     * 会为部署的每个环境使用单独的加密密钥，并使用随机字符作为密钥。
     * （2）直接在 Docker 文件中硬编码了 ENCRYPT_KEY 环境变量。这样做是为了让你可以下载文件并启动它们而无
     * 需设置环境变量。在真实的运行时环境中，将引用 ENCRYPT_KEY 作为 Docker 文件中的一个操作系统环境变量。
     * 注意这一点，并且不要在 Dockerfile 内硬编码加密密钥。记住，Dockerfile 应该处于源代码管理下。
     *
     *
     *
     * 3、加密和解密属性
     *
     * 现在，可以开始加密在 Spring Cloud Config 中使用的属性了。这里将加密用于访问 EagleEye 数据的许可
     * 证服务 Postgres 数据库密码。要加密的属性是 spring.datasource.password，其当前设置的纯文本值为
     * p0stgr@s。
     *
     * 在启动 Spring Cloud Config 实例时，Spring Cloud Config 将检测到环境变量 ENCRYPT_KEY 已设置，
     * 并自动将两个新端点（/encrypt 和 /decrypt）添加到 Spring Cloud Config 服务。这里将使用 /encrypt
     * 端点加密 p0stgr@s 值。
     *
     * 请注意，无论何时调用 /encrypt 或 /decrypt 端点，都需要确保对这些端点进行 POST 请求。
     *
     * 如果要解密这个值，可以使用 /decrypt 端点，在调用中传递已加密的字符串。
     *
     * 现在可以使用以下语法将已加密的属性添加到 GitHub 或基于文件系统的许可证服务的配置文件中：
     *
     * spring.datasource.password:"{cipher}
     * ➥  858201e10fe3c9513e1d28b33ff417a66e8c8411dcff3077c53cf53d8a1be360"
     *
     * Spring Cloud 配置服务器要求所有已加密的属性前面加上 {cipher}。{cipher} 告诉 Spring Cloud 配置
     * 服务器它正在处理已加密的值。启动 Spring Cloud 配置服务器，并使用 GET 方法访问 http://localhost
     * :8888/licensingservice/default 端点。
     *
     * 这里通过对属性进行加密来让 spring.datasource.password 变得更安全，但仍然存在一个问题。
     *
     * 在访问 http://localhost:8888/licensingservice/default 端点时，数据库密码被以纯文本形式公开了。
     *
     * PS：虽然在属性文件中，spring.datasource.password 已经被加密，然而当许可证服务的配置被检索时，它
     * 将被解密。这仍然是有问题的。
     *
     * 在默认情况下，Spring Cloud Config 将在服务器上解密所有属性，并将未加密的纯文本作为结果传回给请求
     * 属性的应用程序。但是，开发人员可以告诉 Spring Cloud Config 不要在服务器上进行解密，并让应用程序
     * 负责检索配置数据以解密已加密的属性。
     *
     *
     *
     * 4、配置微服务以在客户端使用加密
     *
     * 要让客户端对属性进行解密，需要做以下三件事情。
     * （1）配置 Spring Cloud Config 不要在服务器端解密属性。
     * （2）在许可证服务器上设置对称密钥。
     * （3）将 spring-security-rsa JAR 添加到许可证服务的 pom.xml 文件中。
     *
     * 首先需要做的是在 Spring Cloud Config 中禁用服务器端的属性解密。这可以通过设置 Spring Cloud Config
     * 的 src/main/resources/application.yml 文件中的spring.cloud.config.server.encrypt.enabled
     * 属性为 false 来完成。这就是在 Spring Cloud Config 服务器上需要做的所有工作。
     *
     * 因为许可证服务现在负责解密已加密的属性，所以需要先在许可证服务上设置对称密钥，方法是确保 ENCRYPT_KEY
     * 环境变量与 Spring Cloud Config 服务器使用的对称密钥相同（如 IMSYMMETRIC）。
     *
     * 接下来，需要在许可证服务中包含 spring-security-rsa JAR 依赖项：
     *
     * <dependency>
     *   <groupId>org.springframework.security</groupId>
     *   <artifactId>spring-security-rsa</artifactId>
     * </dependency>
     *
     * 这些 JAR 文件包含解密从 Spring Cloud Config 检索的已加密的属性所需的 Spring 代码。有了这些更改，
     * 就可以启动 Spring Cloud Config 和许可证服务了。
     *
     * 如果你访问 http://localhost:8888/licensingservice/default 端点，就会发现 spring.datasource
     * .password 是以加密形式返回的。
     *
     * PS：启用客户端解密后，敏感属性不再以未加密文本的形式从 Spring Cloud Config REST 调用中返回。相反，
     * 在从 Spring Cloud Config 加载属性时，该属性将由调用服务解密。
     */
    public static void main(String[] args) {

    }

}
