package com.fcgo.eft.sutra.exception;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodArgumentNotValid {
    private String message;
    private List<FieldError> fieldErrors;
}


