package com.siwuxie095.spring.cloud.chapter7th.example7th;

/**
 * @author Jiajing Li
 * @date 2021-06-17 22:17:15
 */
public class Main {

    /**
     * 小结
     *
     * （1）OAuth2 是一个基于令牌的身份认证框架，它用来验证用户。
     * （2）OAuth2 确保每个微服务执行用户请求不需要提供每个调用的用户凭证。
     * （3）OAuth2 提供不同的机制来保护 Web 服务调用。这些机制称为授权。
     * （4）在 Spring 中使用 OAuth2，你需要创建一个 OAuth2 认证服务。
     * （5）想要调用服务的每个应用程序都需要在 OAuth2 认证服务中注册。
     * （6）每个应用程序都有自己的应用程序名称和密钥。
     * （7）用户凭证和角色是在内存或数据存储，且通过 Spring Security 访问。
     * （8）每个服务必须定义角色可以采取的操作。
     * （9）Spring Cloud Security 支持 JavaScript Web Token（JWT）规范。
     * （10）JWT 定义了一个签名，生成 OAuth2 令牌的 JavaScript 标准。
     * （11）使用 JWT，你可以将自定义字段注入到规范中。
     * （12）保护你的微服务涉及的不仅仅是使用 OAuth2。你应该使用 HTTPS 来加密
     * 服务间的所有调用。
     * （13）使用服务网关来缩小通过服务可以到达的接入点的数量。
     * （14）通过限制服务正在运行的操作系统上的入站和出站端口的数量来限制服务的
     * 攻击面。
     */
    public static void main(String[] args) {

    }

}
