package com.starcloud.vertx.spring.eventbus;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryContext;

/**
 * @author zhongwenjian
 * @date 2022/8/13
 */
public interface EventbusInboundInterceptor<T> extends Handler<DeliveryContext<T>> {

    default void handle(DeliveryContext<T> context) {
        context.next();
    }
}
