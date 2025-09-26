package com.fcgo.eft.sutra.exception;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldError {
    private String name;
    private String message;
}
