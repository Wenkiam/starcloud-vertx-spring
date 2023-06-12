package starcloud.vertx.spring.example;

import starcloud.vertx.spring.anotation.VerticleScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhongwenjian
 * @date 2022/8/12
 */
@SpringBootApplication
@VerticleScan
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
