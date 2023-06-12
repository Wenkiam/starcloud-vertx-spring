package starcloud.vertx.zookeeper.spring.autoconfigure;

import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author zhongwenjian
 * @date 2022/7/30
 */
@Configuration
@ConditionalOnClass({ZookeeperClusterManager.class, CuratorFramework.class})
@EnableConfigurationProperties(CuratorProperties.class)
public class VertxZookeeperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ClusterManager.class)
    public ZookeeperClusterManager clusterManager(CuratorFramework curatorFramework) {
        return new ZookeeperClusterManager(curatorFramework);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "vertx.zookeeper.curator.hosts")
    public CuratorFramework curator(CuratorProperties properties, ObjectProvider<CuratorCustomizer> customizers)
        throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
            properties.getBaseSleepTimeMs(),
            properties.getMaxRetries(),
            properties.getMaxSleepTimeMs());
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
            .connectString(String.join(",", properties.getHosts()))
            .namespace(properties.getRootPath())
            .sessionTimeoutMs(properties.getSessionTimeout())
            .connectionTimeoutMs(properties.getConnectTimeout())
            .retryPolicy(retryPolicy);
        customizers.orderedStream().forEach(curatorCustomizer -> curatorCustomizer.customize(builder));
        CuratorFramework curator = builder.build();
        curator.start();
        if (StringUtils.hasText(properties.getAuth())) {
            curator.getZookeeperClient().getZooKeeper().addAuthInfo("digest", properties.getAuth().getBytes());
        }
        return curator;
    }
}
