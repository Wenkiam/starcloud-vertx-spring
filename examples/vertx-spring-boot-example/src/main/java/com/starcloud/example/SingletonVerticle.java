package com.starcloud.example;

import io.vertx.core.AbstractVerticle;
import org.springframework.stereotype.Component;

/**
 * @author zhongwenjian
 * @date 2022/8/12
 */
@Component
public class SingletonVerticle extends AbstractVerticle {

    public void start() {
        System.out.println("singleton verticle deployed and started success");
    }
}
