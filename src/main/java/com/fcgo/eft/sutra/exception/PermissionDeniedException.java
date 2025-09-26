package com.fcgo.eft.sutra.exception;

import lombok.Getter;

@Getter
public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException() {
        super("You do not have permission to access this feature.", null, false, false);
    }

    private final ErrorMessage dto = ErrorMessage.builder().message("You do not have permission to access this feature.").code(403).build();

}
