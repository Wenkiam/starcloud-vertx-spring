package starcloud.vertx.spring.deploy;


import starcloud.vertx.spring.anotation.DeployOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author zhongwenjian
 * @date 2022/7/24
 */
class VerticleDeployer {

    private static final Logger log = LoggerFactory.getLogger(VerticleDeployer.class);

    private final Map<String, Verticle> unDeployed = new HashMap<>();

    private Vertx vertx;

    void setVertx(Vertx vertx) {
        this.vertx = vertx;
        unDeployed.forEach(this::deploy);
        unDeployed.clear();
    }

    void deploy(String name, Verticle verticle) {
        if (vertx == null) {
            unDeployed.put(name, verticle);
            return;
        }
        DeployOptions options = AnnotatedElementUtils
            .findMergedAnnotation(verticle.getClass(), DeployOptions.class);

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        if (options != null) {
            deploymentOptions.setHa(options.ha());
            deploymentOptions.setWorker(options.worker());
            deploymentOptions.setWorkerPoolName(options.workerPoolName());
            deploymentOptions.setWorkerPoolSize(options.workerPoolSize());
            deploymentOptions.setMaxWorkerExecuteTime(options.maxWorkerExecuteTime());
            deploymentOptions.setMaxWorkerExecuteTimeUnit(TimeUnit.MILLISECONDS);
        }
        vertx.deployVerticle(verticle, deploymentOptions).onComplete(ar->{
            if (ar.succeeded()) {
                log.info("deploy {} success", name);
            } else {
                log.error("deploy {} failed", name, ar.cause());
            }
        });
    }

}
