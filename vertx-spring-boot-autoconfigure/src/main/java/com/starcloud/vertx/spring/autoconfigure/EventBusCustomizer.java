package com.starcloud.vertx.spring.autoconfigure;

import io.vertx.core.eventbus.EventBus;

/**
 * @author zhongwenjian
 * @date 2022/7/24
 */
public interface EventBusCustomizer {

    /**
     * customize event bus
     * @param eventBus event bus
     */
    void customize(EventBus eventBus);
}
