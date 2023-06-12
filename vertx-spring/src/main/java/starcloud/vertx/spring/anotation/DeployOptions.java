package starcloud.vertx.spring.anotation;

import io.vertx.core.VertxOptions;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhongwenjian
 * @date 2022/7/23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeployOptions {

    int instances() default 1;

    String name() default "";

    boolean ha() default false;

    boolean worker() default false;

    String workerPoolName() default "";

    int workerPoolSize() default VertxOptions.DEFAULT_WORKER_POOL_SIZE;

    int maxWorkerExecuteTime() default 60000;
}
