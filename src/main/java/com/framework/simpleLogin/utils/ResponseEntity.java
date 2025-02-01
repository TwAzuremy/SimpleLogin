package com.framework.simpleLogin.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.framework.simpleLogin.serializer.HttpStatusSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEntity<T> {
    @JsonSerialize(using = HttpStatusSerializer.class)
    private HttpStatus status;
    private String message;
    private T data;

    @JsonIgnore
    private HttpHeaders headers = new HttpHeaders();

    public ResponseEntity(HttpStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseEntity(HttpStatus status, T data) {
        this.status = status;
        this.message = status.getReasonPhrase();
        this.data = data;
    }

    public ResponseEntity<?> addHeader(String name, String value) {
        this.headers.add(name, value);

        return this;
    }
}
