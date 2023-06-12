package starcloud.vertx.spring.anotation;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author zhongwenjian
 * @date 2022/7/23
 */
public class VerticleClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    private final BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

    private final BeanDefinitionRegistry registry;

    private final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    public VerticleClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        addIncludeFilter(new AnnotationTypeFilter(DeployOptions.class));
        addExcludeFilter(new AnnotationTypeFilter(Component.class));
        this.registry = registry;
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {

                if (!(candidate instanceof AnnotatedBeanDefinition)){
                    continue;
                }
                Assert.isTrue(isVerticle(((AnnotatedBeanDefinition)candidate).getMetadata()),
                    "@DeployOptions can only be used on a Verticle class");
                AnnotationMetadata annotationMetadata = ((AnnotatedBeanDefinition)candidate).getMetadata();
                Map<String, Object> attributes = annotationMetadata
                    .getAnnotationAttributes(
                        DeployOptions.class.getCanonicalName());
                beanDefinitions.addAll(register(attributes, candidate));
            }
        }
        return beanDefinitions;
    }
    private List<BeanDefinitionHolder> register(Map<String, Object> attributes, BeanDefinition candidate) {
        int instances = (int) attributes.get("instances");
        if (instances < 1) {
            throw new IllegalArgumentException("attribute 'instances' must be positive");
        }
        String beanName = (String)attributes.get("name");
        if (!StringUtils.hasText(beanName)) {
            beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
        }
        List<BeanDefinitionHolder> holders = new ArrayList<>(instances);
        if (instances == 1) {
            holders.add(register(candidate, beanName));
            return holders;
        }
        for (int i=0; i< instances; i++){
            String name = beanName+"_"+i;
            holders.add(register(candidate, name));
        }
        return holders;
    }

    private BeanDefinitionHolder register(BeanDefinition candidate, String beanName) {
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
        candidate.setScope(scopeMetadata.getScopeName());

        if (candidate instanceof AbstractBeanDefinition) {
            postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
        }
        AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
        if (checkCandidate(beanName, candidate)) {
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
            definitionHolder = applyScopedProxyMode(scopeMetadata, definitionHolder);
            registerBeanDefinition(definitionHolder, this.registry);
            return definitionHolder;
        }
        return null;
    }
    private BeanDefinitionHolder applyScopedProxyMode(
        ScopeMetadata metadata, BeanDefinitionHolder definition) {

        ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
        if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        }
        boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
        return ScopedProxyUtils.createScopedProxy(definition, registry, proxyTargetClass);
    }
    private boolean isVerticle(AnnotationMetadata metadata) {
        String superClass = metadata.getSuperClassName();
        if (AbstractVerticle.class.getName().equals(superClass)) {
            return true;
        }
        String[] interfaces = metadata.getInterfaceNames();
        for (String name:interfaces) {
            if (Verticle.class.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
