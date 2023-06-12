package starcloud.vertx.zookeeper.spring.autoconfigure;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhongwenjian
 * @date 2022/7/30
 */
@ConfigurationProperties("vertx.zookeeper.curator")
public class CuratorProperties {

    private String rootPath = "io.vertx";

    private int sessionTimeout = 20000;

    private int connectTimeout = 20000;

    private List<String> hosts;

    private int baseSleepTimeMs;

    private int maxSleepTimeMs;

    private int maxRetries;

    private String auth;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxSleepTimeMs() {
        return maxSleepTimeMs;
    }

    public void setMaxSleepTimeMs(int maxSleepTimeMs) {
        this.maxSleepTimeMs = maxSleepTimeMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
