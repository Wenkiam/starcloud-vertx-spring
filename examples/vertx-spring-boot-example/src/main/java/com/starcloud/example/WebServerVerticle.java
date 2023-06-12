package com.starcloud.example;

import com.starcloud.vertx.spring.anotation.DeployOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author zhongwenjian
 * @date 2022/8/12
 */
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
