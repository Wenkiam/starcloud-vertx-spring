本框架可以让你通过spring boot的方式使用vertx，让你更简单地开发基于vertx的异步应用。

### 快速开始

创建一个spring boot 应用，引入下面的依赖
```xml
<dependency>
      <groupId>io.github.wenkiam</groupId>
      <artifactId>vertx-spring-boot-starter</artifactId>
      <version>1.0</version>
</dependency>
```
在spring boot启动类里加上@VerticleScan注解
```java
@SpringBootApplication
@VerticleScan
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

编写你自己的verticle，加上@DeployOptions注解，或者将verticle注册为spring bean，vertx会自动部署它们
```java
@DeployOptions(instances = 4)
public class WebServerVerticle extends AbstractVerticle {

    @Value("${vertx.http.server.port:8080}")
    private int port;
    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route("/").handler(context-> {
            context.response().end("hello world");
        });
        
        vertx.createHttpServer().requestHandler(router).listen(port).onSuccess(s->{
            System.out.println("web server started success, listen on "+port);
        }).onFailure(Throwable::printStackTrace);
    }
}
```
可以在spring boot配置文件里定制 vertx 启动参数
```properties
vertx.options.eventLoopPoolSize=4
vertx.options.worker-pool-size=10
vertx.options.eventBusOptions.idle-timeout=1000
...
```
具体示例代码可参考example里面的示例项目