package com.starcloud.vertx.spring.autoconfigure;

import com.starcloud.vertx.spring.cluster.ClusterManagerInvocationHandler;
import com.starcloud.vertx.spring.deploy.VerticleDeployBeanPostProcessor;
import com.starcloud.vertx.spring.eventbus.EventbusInboundInterceptor;
import com.starcloud.vertx.spring.eventbus.EventbusOutBoundInterceptor;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.spi.VertxThreadFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.spi.cluster.NodeSelector;
import io.vertx.core.spi.metrics.VertxMetrics;
import io.vertx.core.spi.tracing.VertxTracer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhongwenjian
 * @date 2022/7/24
 */
@Configuration
@ConditionalOnClass(Vertx.class)
public class VertxAutoConfiguration {

    @Bean
    public static VerticleDeployBeanPostProcessor verticleDeployBeanPostProcessor () {
        return new VerticleDeployBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "vertx.options")
    public VertxOptions vertxOptions() {
        return new VertxOptions();
    }
    @Bean
    @ConditionalOnMissingBean
    public Vertx vertx(VertxOptions options,
        ObjectProvider<VertxOptionsCustomizer> vertxOptionsCustomizers,
        ObjectProvider<VertxBuilderCustomizer> vertxBuilderCustomizers) throws ExecutionException, InterruptedException {
        vertxOptionsCustomizers.orderedStream().forEach(customizer->customizer.customize(options));
        VertxBuilder builder = new VertxBuilder(options).init();
        vertxBuilderCustomizers.orderedStream().forEach(vertxBuilderCustomizer -> vertxBuilderCustomizer.customize(builder));
        ClusterManager clusterManager = builder.clusterManager();
        if (clusterManager == null) {
            return builder.vertx();
        }
        CompletableFuture<Vertx> future = new CompletableFuture<>();
        Promise<Vertx> vertxPromise = Promise.promise();
        builder.clusteredVertx(vertxPromise);
        vertxPromise.future().onSuccess(future::complete).onFailure(future::completeExceptionally);
        return future.get();
    }

    @Bean
    public EventBus eventBus(Vertx vertx, ObjectProvider<EventBusCustomizer> provider) {
        EventBus eventBus = vertx.eventBus();
        provider.orderedStream().forEach(eventBusCustomizer -> eventBusCustomizer.customize(eventBus));
        return eventBus;
    }

    @Configuration
    static class VertxCustomizeConfiguration {
        @Bean
        @ConditionalOnProperty(value = "vertx.cluster.manager.proxy", havingValue = "true")
        public VertxOptionsCustomizer clusterManagerProxySetter(ObjectProvider<ClusterManager> provider) {
            return options-> provider.ifAvailable(clusterManager -> {
                clusterManager = ClusterManagerInvocationHandler.getProxy(clusterManager);
                options.setClusterManager(clusterManager);
            });
        }
        @Bean
        @ConditionalOnProperty(value = "vertx.cluster.manager.proxy", havingValue = "false", matchIfMissing = true)
        public VertxOptionsCustomizer clusterManagerSetter(ObjectProvider<ClusterManager> provider) {
            return options-> provider.ifAvailable(options::setClusterManager);
        }

        @Bean
        public VertxBuilderCustomizer vertxMetricsSetter(ObjectProvider<VertxMetrics> metrics) {
            return builder -> metrics.ifAvailable(builder::metrics);
        }

        @Bean
        public VertxBuilderCustomizer vertxNodeSelectorSetter(ObjectProvider<NodeSelector> provider) {
            return builder -> provider.ifAvailable(builder::clusterNodeSelector);
        }

        @Bean
        public VertxBuilderCustomizer vertxTracerSetter(ObjectProvider<VertxTracer<?, ?>> provider) {
            return builder -> provider.ifAvailable(builder::tracer);
        }

        @Bean
        public VertxBuilderCustomizer vertxThreadFactorySetter(ObjectProvider<VertxThreadFactory> provider) {
            return vertxBuilder -> provider.ifAvailable(vertxBuilder::threadFactory);
        }

        @Bean
        public EventBusCustomizer eventbusOutboundInterceptorsInitializer(ObjectProvider<EventbusOutBoundInterceptor<?>> interceptors) {
            return eventBus -> interceptors.orderedStream().forEach(eventBus::addOutboundInterceptor);
        }

        @Bean
        public EventBusCustomizer eventbusInboundInterceptorsInitializer(ObjectProvider<EventbusInboundInterceptor<?>> interceptors) {
            return eventBus -> interceptors.orderedStream().forEach(eventBus::addInboundInterceptor);
        }

    }
}
