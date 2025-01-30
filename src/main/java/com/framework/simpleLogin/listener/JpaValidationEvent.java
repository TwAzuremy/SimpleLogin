package com.framework.simpleLogin.listener;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class JpaValidationEvent extends ApplicationEvent {
    private String message;
    private boolean success;

    public JpaValidationEvent(Object source, String message, boolean success) {
        super(source);

        this.message = message;
        this.success = success;
    }
}
