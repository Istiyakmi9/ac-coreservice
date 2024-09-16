package com.bot.coreservice.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationException extends RuntimeException {
    String userErrorMessage;
    int StatusCode;

    public ApplicationException(String message, Exception ex) {
        super(message, ex);
    }
    public ApplicationException(String message) {
        super(message);
    }

    public static ApplicationException ThrowBadRequest(String message, Exception ex) {
        var exception = new ApplicationException(message, ex);
        exception.setStatusCode(HttpStatus.BAD_REQUEST.value());
        exception.setUserErrorMessage(message);
        return exception;
    }
}
