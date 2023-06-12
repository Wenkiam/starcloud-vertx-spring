package starcloud.vertx.zookeeper.spring.autoconfigure;

import org.apache.curator.framework.CuratorFrameworkFactory;

/**
 * @author zhongwenjian
 * @date 2022/7/30 19:19
 */
public interface CuratorCustomizer {

    /**
     * customize curator frame factory builder
     * @param builder curator frame factory builder
     */
    void customize(CuratorFrameworkFactory.Builder builder);
}
