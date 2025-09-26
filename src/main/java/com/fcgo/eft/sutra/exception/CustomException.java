package com.fcgo.eft.sutra.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorMessage dto = ErrorMessage.builder().build();

    public CustomException(String message) {
        super(message, null, false, false);
        dto.setMessage(message);
        dto.setCode(407);
    }
}
