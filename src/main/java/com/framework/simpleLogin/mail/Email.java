package com.framework.simpleLogin.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email {
    private String recipient;
    private String msgBody;
    private Boolean isHtml = false;
    private String subject;
    private String attachment;

    public void setMsgBody(String msgBody, Boolean isHtml) {
        this.msgBody = msgBody;
        this.isHtml = isHtml;
    }
}
