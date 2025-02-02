package com.framework.simpleLogin.dto;

import com.framework.simpleLogin.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCaptchaRequest {
    private User user;
    private String captcha;
    private Object attachment;
}
