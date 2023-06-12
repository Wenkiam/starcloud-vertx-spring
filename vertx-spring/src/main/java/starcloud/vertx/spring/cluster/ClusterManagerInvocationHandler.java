package starcloud.vertx.spring.cluster;

import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.spi.cluster.NodeListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 大部分的cluster实现在调用nodeListener方法时是替换原先的nodeListener，
 * 这个代理实现采用观察者模式，调用nodeListener方法是注册一个listener事件，
 * 有节点变化时可以通知到每一个listener
 * @author zhongwenjian
 * @date 2022/8/5
 */
public class ClusterManagerInvocationHandler implements InvocationHandler {

    private final ClusterManager delegate;
    private final CompositeNodeListener listeners = new CompositeNodeListener();

    public ClusterManagerInvocationHandler(ClusterManager delegate) {
        this.delegate = delegate;
        delegate.nodeListener(listeners);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = "nodeListener";
        if (methodName.equals(method.getName()) && args != null
            && args.length==1 && args[0] instanceof NodeListener) {
            listeners.add((NodeListener)args[0]);
            return null;
        }
        methodName = "remove";
        if (methodName.equals(method.getName()) && args != null
            && args.length==1 && args[0] instanceof NodeListener) {
            return listeners.remove((NodeListener)args[0]);
        }

        return method.invoke(delegate, args);
    }

    public static ClusterManager getProxy(ClusterManager clusterManager) {
        return (ClusterManager) Proxy.newProxyInstance(clusterManager.getClass().getClassLoader(),
                new Class[]{ClusterManager.class, Removable.class}, new ClusterManagerInvocationHandler(clusterManager));
    }

    public interface Removable {
        boolean remove(NodeListener listener);
    }
}
