package com.framework.simpleLogin.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.framework.simpleLogin.serializer.HttpStatusSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEntity<T> {
    @JsonSerialize(using = HttpStatusSerializer.class)
    private HttpStatus status;
    private String message;
    private T data;

    public ResponseEntity(HttpStatus status, T data) {
        this.status = status;
        this.message = status.getReasonPhrase();
        this.data = data;
    }
}
