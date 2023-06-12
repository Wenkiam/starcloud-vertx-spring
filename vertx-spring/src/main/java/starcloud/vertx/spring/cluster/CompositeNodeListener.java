package starcloud.vertx.spring.cluster;

import io.vertx.core.spi.cluster.NodeListener;
import java.util.ArrayList;

/**
 * @author zhongwenjian
 * @date 2022/8/5
 */
public class CompositeNodeListener extends ArrayList<NodeListener> implements NodeListener {
    @Override
    public void nodeAdded(String nodeID) {
        forEach(listener->listener.nodeAdded(nodeID));
    }

    @Override
    public void nodeLeft(String nodeID) {
        forEach(listener->listener.nodeLeft(nodeID));
    }
}
