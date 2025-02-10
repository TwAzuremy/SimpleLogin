package com.framework.simpleLogin.annotation.processor;

import com.framework.simpleLogin.annotation.OAuth2ClientValue;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2ClientValuePostProcessor implements BeanPostProcessor {
    private final Environment environment;

    public OAuth2ClientValuePostProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            OAuth2ClientValue annotation = field.getAnnotation(OAuth2ClientValue.class);

            if (annotation != null) {
                String registrationPrefix = annotation.prefix() + ".registration." + annotation.key();
                String providerPrefix = annotation.prefix() + ".provider." + annotation.key();

                @SuppressWarnings("unchecked")
                Map<String, Object> registrationMap = Binder.get(environment)
                        .bind(registrationPrefix, Map.class)
                        .orElseGet(HashMap::new);

                @SuppressWarnings("unchecked")
                Map<String, Object> providerMap = Binder.get(environment)
                        .bind(providerPrefix, Map.class)
                        .orElseGet(HashMap::new);

                Map<String, String> values = new HashMap<>();

                registrationMap.forEach((key, value) -> values.put(key, value.toString()));
                providerMap.forEach((key, value) -> values.put(key, value.toString()));
                values.put("id", annotation.key());

                field.setAccessible(true);
                field.set(bean, values);
            }
        });

        return bean;
    }
}
