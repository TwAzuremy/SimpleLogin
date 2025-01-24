package com.framework.simpleLogin.dto;

import com.framework.simpleLogin.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private int id;
    private String username;
    private String email;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
