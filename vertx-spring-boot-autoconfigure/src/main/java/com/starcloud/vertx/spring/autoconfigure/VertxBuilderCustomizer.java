package com.starcloud.vertx.spring.autoconfigure;

import io.vertx.core.impl.VertxBuilder;

/**
 * @author zhongwenjian
 * @date 2022/7/24
 */
public interface VertxBuilderCustomizer {

    /**
     * customize vertx builder
     * @param vertxBuilder vertx builder
     */
    void customize(VertxBuilder vertxBuilder);
}
