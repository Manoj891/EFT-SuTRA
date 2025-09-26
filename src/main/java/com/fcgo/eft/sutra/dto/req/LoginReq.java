package com.fcgo.eft.sutra.dto.req;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LoginReq {
    private String username;
    private String password;
}
