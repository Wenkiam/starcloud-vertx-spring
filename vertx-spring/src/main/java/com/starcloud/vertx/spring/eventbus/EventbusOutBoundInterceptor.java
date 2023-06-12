package com.starcloud.vertx.spring.eventbus;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryContext;

/**
 * @author zhongwenjian
 * @date 2022/8/13
 */
public interface EventbusOutBoundInterceptor<T> extends Handler<DeliveryContext<T>> {

    @Override
    default void handle(DeliveryContext<T> context) {
        context.next();
    }
}
