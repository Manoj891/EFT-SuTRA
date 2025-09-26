package com.fcgo.eft.sutra.security;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticatedUser {
    private String id;
    private String email;
    private String username;
    private String ipAddress;
    private String appName;
    private String paymentUser;
    private String deploymentType;

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\",\"email\":\"" + email + "\",\"username\":\"" + username + ",\"ipAddress\":\"" + ipAddress + ",\"appName\":\"" + appName + "\",\"paymentUser\":\"" + paymentUser + "\"}";
    }
}

