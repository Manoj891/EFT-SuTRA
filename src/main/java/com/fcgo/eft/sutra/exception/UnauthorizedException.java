package com.fcgo.eft.sutra.exception;

import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;


@Getter
public class UnauthorizedException extends BadCredentialsException {
    public UnauthorizedException() {
        super("The request has not been applied because it lacks valid authentication credentials.");
    }

    private final ErrorMessage dto = ErrorMessage.builder().message("The request has not been applied because it lacks valid authentication credentials.").code(401).build();
}
