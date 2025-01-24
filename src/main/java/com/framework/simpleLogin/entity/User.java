package com.framework.simpleLogin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", length = 64, nullable = false)
    private String username;

    // TODO:
    //      (`password` + `slat`) use SHA-512: Stitch the password with the salt first, and then use SHA-512 encryption ( length = 64 ).
    //      `slat` use SecureRandom: After generating 32 bytes, switch to Base64 encoding ( length = 44 ).
    //      finally: string + slat ( Base64 ) ( length = 108 ).
    @Column(name = "password", length = 108, nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    @Pattern(regexp = "^\\S+@\\S+\\.\\S+$", message = "Invalid email address")
    private String email;
}
