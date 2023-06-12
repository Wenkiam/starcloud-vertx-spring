package com.starcloud.vertx.spring.autoconfigure;

import io.vertx.core.VertxOptions;

/**
 * @author zhongwenjian
 * @date 2022/7/24
 */
public interface VertxOptionsCustomizer {
    /**
     * customize vertx init options
     * @param options vertx init options
     */
    void customize(VertxOptions options);
}
