package com.siwuxie095.spring.cloud.chapter6th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-11 21:51:48
 */
public class Main {

    /**
     * Zuul 的真正威力：过滤器
     *
     * 虽然通过 Zuul 网关代理所有请求确实可以简化服务调用，但是在想要编写应用于所有流经网关的服务调用的自定义逻辑时，
     * Zuul 的真正威力才发挥出来。在大多数情况下，这种自定义逻辑用于强制执行一组一致的应用程序策略，如安全性、日志
     * 记录和对所有服务的跟踪。
     *
     * 这些应用程序策略被认为是横切关注点，因为开发人员希望将它们应用于应用程序中的所有服务，而无需修改每个服务来实
     * 现它们。通过这种方式，Zuul 过滤器可以按照与 J2EE servlet 过滤器或 Spring Aspect 类似的方式来使用。这种
     * 方式可以拦截大量行为，并且在原始编码人员意识不到变化的情况下，对调用的行为进行装饰或更改。servlet 过滤器或
     * Spring Aspect 被本地化为特定的服务，而使用 Zuul 和 Zuul 过滤器允许开发人员为通过 Zuul 路由的所有服务实
     * 现横切关注点。
     *
     * Zuul 允许开发人员使用 Zuul 网关内的过滤器构建自定义逻辑。过滤器可用于实现每个服务请求在执行时都会经过的业务
     * 逻辑链。
     *
     * Zuul 支持以下三种类型的过滤器。
     * （1）前置过滤器：前置过滤器在 Zuul 将实际请求发送到目的地之前被调用。前置过滤器通常执行确保服务具有一致的消
     * 息格式（例如，关键的 HTTP 首部是否设置妥当）的任务，或者充当看门人，确保调用该服务的用户已通过验证（他们的
     * 身份与他们声称的一致）和授权（他们可以做他们请求做的）。
     * （2）后置过滤器：后置过滤器在目标服务被调用并将响应发送回客户端后被调用。通常后置过滤器会用来记录从目标服务
     * 返回的响应、处理错误或审核对敏感信息的响应。
     * （3）路由过滤器：路由过滤器用于在调用目标服务之前拦截调用。通常使用路由过滤器来确定是否需要进行某些级别的动态
     * 路由。例如，后续将使用路由级别的过滤器，该过滤器将在同一服务的两个不同版本之间进行路由，以便将一小部分的服务
     * 调用路由到服务的新版本，而不是路由到现有的服务。这样就能够在不让每个人都使用新服务的情况下，让少量的用户体验
     * 新功能。
     *
     * 如下展示了在处理服务客户端请求时，前置过滤器、后置过滤器和路由过滤器如何组合在一起。
     * （1）前置过滤器：当传入的请求进入 Zuul 时，前置过滤器被执行。
     * （2）路由过滤器：路由过滤器允许开发人员覆盖 Zuul 的默认路由逻辑，并将用户路由到他们需要去的地方。
     * （3）动态路由：路由过滤器可以动态地路由到 Zuul 之外的服务。
     * （4）目标路由：最后，Zuul 将确定目标路由，并将请求发送到它的目的地。
     * （5）后置过滤器：在调用目标服务之后，来自目标服务返回的响应将流经 Zuul 后置过滤器。
     *
     * PS：前置过滤器、路由过滤器和后置过滤器组成了客户端请求流经的管道。随着请求进入 Zuul，这些过滤器可以处理传入
     * 的请求。
     *
     * 如果遵循上面所列出的流程，将会看到所有的事情都是从服务客户端调用服务网关公开的服务开始的。从这里开始，发生了
     * 以下活动。
     * （1）在请求进入 Zuul 网关时，Zuul 调用所有在 Zuul 网关中定义的前置过滤器。前置过滤器可以在 HTTP 请求到达
     * 实际服务之前对 HTTP 请求进行检查和修改。前置过滤器不能将用户重定向到不同的端点或服务。
     * （2）在针对 Zuul 的传入请求执行前置过滤器之后，Zuul 将执行已定义的路由过滤器。路由过滤器可以更改服务所指向
     * 的目的地。
     * （3）路由过滤器可以将服务调用重定向到 Zuul 服务器被配置的发送路由以外的位置。但 Zuul 路由过滤器不会执行
     * HTTP 重定向，而是会终止传入的 HTTP 请求，然后代表原始调用者调用路由。这意味着路由过滤器必须完全负责动态
     * 路由的调用，并且不能执行 HTTP 重定向。
     * （4）如果路由过滤器没有动态地将调用者重定向到新路由，Zuul 服务器将发送到最初的目标服务的路由。
     * （5）目标服务被调用后，Zuul 后置过滤器将被调用。后置过滤器可以检查和修改来自被调用服务的响应。
     *
     * 了解如何实现 Zuul 过滤器的最佳方法就是使用它们。为此，后续将构建前置过滤器、路由过滤器和后置过滤器，然后通过
     * 它们运行服务客户端请求。
     *
     * 如下展示了如何将这些过滤器组合在一起以处理对 EagleEye 服务的请求。
     * （1）服务客户端：服务客户端通过 Zuul 调用服务。
     * （2）前置过滤器：TrackingFilter 将检查每个传入的请求，如果关联 ID 不存在，那么将会在 HTTP
     * 首部中创建一个关联 ID。
     * （3）路由过滤器：SpecialRoutesFilter 将决定是否要将一定比例的路由发送到不同的服务。
     * （4）后置过滤器：ResponseFilter 将确保从 Zuul 发回的每个响应在 HTTP 首部中包含关联 ID。
     *
     * PS：Zuul 过滤器提供对服务调用、日志记录和动态路由的集中跟踪。Zuul 过滤器允许开发人员针对
     * 微服务调用执行自定义规则和策略。
     *
     * 按照如上所示的流程，会看到以下过滤器被使用。
     * （1）TrackingFilter：TrackingFilter 是一个前置过滤器，它确保从 Zuul 流出的每个请求都具有相关的
     * 关联 ID。关联 ID 是在执行客户请求时执行的所有微服务中都会携带的唯一 ID。关联 ID 用于跟踪一个调用经
     * 过一系列微服务调用发生的事件链。
     * （2）SpecialRoutesFilter：SpecialRoutesFilter 是一个 Zuul 路由过滤器，它将检查传入的路由，并
     * 确定是否要在该路由上进行 A/B 测试。A/B 测试是一种技术，在这种技术中，用户（在这种情况下是服务）随机
     * 使用同一个服务提供的两种不同的服务版本。A/B 测试背后的理念是，新功能可以在推出到整个用户群之前进行测
     * 试。在这里的例子中，同一个组织服务将具有两个不同的版本。少数用户将被路由到较新版本的服务，与此同时，
     * 大多数用户将被路由到较旧版本的服务。
     * （3）ResponseFilter：ResponseFilter 是一个后置过滤器，它将把与服务调用相关的关联 ID 注入发送回
     * 客户端的 HTTP 响应首部中。这样，客户端就可以访问与其发出的请求相关联的关联 ID。
     */
    public static void main(String[] args) {

    }

}
