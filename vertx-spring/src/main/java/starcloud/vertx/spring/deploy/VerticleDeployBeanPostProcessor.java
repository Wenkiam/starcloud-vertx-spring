package starcloud.vertx.spring.deploy;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * @author zhongwenjian
 * @date 2022/7/24
 */
public class VerticleDeployBeanPostProcessor implements BeanPostProcessor {

    private final VerticleDeployer deployer = new VerticleDeployer();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Vertx) {
            deployer.setVertx((Vertx)bean);
        } else if (bean instanceof Verticle) {
            deployer.deploy(beanName, (Verticle)bean);
        }
        return bean;
    }

}
