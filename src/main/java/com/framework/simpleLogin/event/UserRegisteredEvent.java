package com.framework.simpleLogin.event;

import com.framework.simpleLogin.dto.UserResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final UserResponse user;

    public UserRegisteredEvent(Object source, UserResponse user) {
        super(source);

        this.user = user;
    }
}
