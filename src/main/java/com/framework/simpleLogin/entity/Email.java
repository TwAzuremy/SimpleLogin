package com.framework.simpleLogin.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
}
