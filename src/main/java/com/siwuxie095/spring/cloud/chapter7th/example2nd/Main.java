package com.siwuxie095.spring.cloud.chapter7th.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-06-14 17:00:04
 */
public class Main {

    /**
     * OAuth2 简介
     *
     * OAuth2 是一个基于令牌的安全验证和授权框架，它将安全性分解为以下四个组成部分。
     * （1）受保护资源：这是开发人员想要保护的资源（在这里的例子中是一个微服务），需要确保只有已通过验证并且具有适当授权
     * 的用户才能访问它。
     * （2）资源所有者：资源所有者定义哪些应用程序可以调用其服务，哪些用户可以访问该服务，以及他们可以使用该服务完成哪些
     * 事情。资源所有者注册的每个应用程序都将获得一个应用程序名称，该应用程序名称与应用程序密钥一起标识应用程序。应用程
     * 序名称和密钥的组合是在验证 OAuth2 令牌时传递的凭据的一部分。
     * （3）应用程序：这是代表用户调用服务的应用程序。毕竟，用户很少直接调用服务。相反，他们依赖应用程序为他们工作。
     * （4）OAuth2 验证服务器：OAuth2 验证服务器是应用程序和正在使用的服务之间的中间人。OAuth2 验证服务器允许用户对
     * 自己进行验证，而不必将用户凭据传递给由应用程序代表用户调用的每个服务。
     *
     * 这四个组成部分互相交互的过程如下：
     * （1）受保护资源：即 想要保护的服务。
     * （2）资源所有者：资源所有者授权哪些应用程序或用户可以通过 OAuth2 服务来访问资源。
     * （3）用户/试图访问受保护资源的应用程序：在用户试图访问受保护的服务时，他们必须进行
     * 验证并从 OAuth2 服务获取一个令牌。
     * （4）OAuth2 验证服务器：OAuth2 服务器对用户进行验证并确认提供给它的令牌。
     *
     * PS：OAuth2 允许用户进行验证，而不必持续提供凭据。
     *
     * 这四个组成部分互相作用对用户进行验证。用户只需提交他们的凭据。如果他们成功通过验证，则会出示一个验证令牌，该令牌
     * 可在服务之间传递。OAuth2 是一个基于令牌的安全框架。针对 OAuth2 服务器，用户通过提供凭据以及用于访问资源的应用
     * 程序来进行验证。如果用户凭据是有效的，那么 OAuth2 服务器就会提供一个令牌，每当用户的应用程序使用的服务试图访问
     * 受保护的资源（微服务）时，就可以提交这个令牌。
     *
     * 接下来，受保护资源可以联系 OAuth2 服务器以确定令牌的有效性，并检索用户授予它们的角色。角色用于将相关用户分组在
     * 一起，并定义用户组 可以访问哪些资源。对于这里来说，将使用 OAuth2 和角色来定义用户可以调用哪些服务端点，以及用
     * 户可以在端点上调用的 HTTP 动词。
     *
     * Web 服务安全是一个极其复杂的主题。开发人员必须了解谁将调用自己的服务（公司网络的内部用户还是外部用户），他们将如
     * 何调用这些服务（是在内部基于 Web 客户端、移动设备还是在企业网络之外的 Web 应用程序），以及他们用代码来完成什么
     * 操作。OAuth2 允许开发人员使用称为授权（grant）的不同验证方案，在不同的场景中保护基于 REST 的服务。OAuth2 规范
     * 具有以下四种类型的授权：
     * （1）密码（password）；
     * （2）客户端凭据（client credential）；
     * （3）授权码（authorization code）；
     * （4）隐式（implicit）。
     *
     * 这里不会逐一介绍每种授权类型，或者为每种授权类型提供代码示例。究其原因，仅仅是因为需要包含的内容太多了。取而代之，
     * 这里将会完成以下事情：
     * （1）讨论微服务如何通过一个较简单的 OAuth2 授权类型（密码授权类型）来使用 OAuth2；
     * （2）使用 JSON Web Token 来提供一个更健壮的 OAuth2 解决方案，并在 OAuth2 令牌中建立一套信息编码的标准；
     * （3）介绍在构建微服务时需要考虑的其他安全注意事项。
     */
    public static void main(String[] args) {

    }

}
