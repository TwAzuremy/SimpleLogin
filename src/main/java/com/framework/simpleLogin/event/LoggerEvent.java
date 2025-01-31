package com.framework.simpleLogin.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class LoggerEvent extends ApplicationEvent {
    private String message;

    public LoggerEvent(Object source, String message) {
        super(source);

        this.message = message;
    }
}
