package com.siwuxie095.spring.cloud.extend2nd.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-06-27 14:10:30
 */
public class Main {

    /**
     * 密码授权
     *
     * OAuth2 密码授权可能是最容易理解的授权类型。这种授权类型适用于应用程序和服务都明确相互信任的时候。例如，
     * EagleEye Web 应用程序和 EagleEye Web 服务（许可证服务和组织服务）都由 ThoughtMechanix 拥有，所
     * 以它们之间存在着一种天然的信任关系。
     *
     * 注意：明确地说，当这里提到 "天然的信任关系" 时，意思是应用程序和服务完全由同一个组织拥有，并且它们是
     * 按照相同的策略和程序来管理的。
     *
     * 当存在一种天然的信任关系时，几乎不用担心将 OAuth2 访问令牌暴露给调用应用程序。例如，EagleEye Web
     * 应用程序可以使用 OAuth2 密码授权来捕获用户凭据，并直接针对 EagleEye OAuth2 服务进行验证。如下展示
     * 了 EagleEye 和下游服务之间的密码授权。
     * （1）在 EagleEye 应用程序可以使用受保护资源之前，它需要在 OAuth2 服务中被唯一标识。通常，应用程序
     * 的所有者通过 OAuth2 服务进行注册，并为其应用程序提供唯一的名称。OAuth2 服务随后提供一个密钥给正在
     * 注册的应用程序。应用程序的名称和由 OAuth2 服务提供的密钥唯一地标识了试图访问任何受保护资源的应用程
     * 序。
     * （2）用户登录到 EagleEye，并将其登录凭据提供给 EagleEye 应用程序。EagleEye 将用户凭据以及应用程
     * 序名称、应用程序密钥直接传给 EagleEye OAuth2 服务。
     * （3）EagleEye OAuth2 服务对应用程序和用户进行验证，然后向用户提供 OAuth2 访问令牌。
     * （4）每次 EagleEye 应用程序代表用户调用服务时，它都会传递 OAuth2 服务器提供的访问令牌。
     * （5）当一个受保护的服务（在本例中是许可证服务和组织服务）被调用时，该服务将回调到 EagleEye OAuth2
     * 服务来确认令牌。如果令牌是有效的，则被调用的服务允许用户继续进行操作。如果令牌无效，OAuth2 服务将
     * 返回 HTTP 状态码 403，指示该令牌无效。
     */
    public static void main(String[] args) {

    }

}
