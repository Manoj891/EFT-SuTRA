package com.fcgo.eft.sutra.dto.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class LoginRes {
    private String token;
    private long expires;
}
