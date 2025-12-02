package com.fcgo.eft.sutra.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "APPLICATION_USER", indexes = {
        @Index(name = "INDEX_APPLICATION_USER_EMAIL", columnList = "EMAIL", unique = true),
        @Index(name = "INDEX_APPLICATION_USER_USERNAME", columnList = "USERNAME", unique = true)
})
public class ApplicationUser {
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "EMAIL", length = 150, nullable = false)
    private String email;
    @Column(name = "USERNAME", length = 150, nullable = false)
    private String username;
    @Column(name = "IP_ADDRESS", length = 30)
    private String ipAddress;
    @Column(name = "APP_NAME", length = 5)
    private String appName;
    @Column(name = "PAYMENT_USER", length = 1)
    private String paymentUser;
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "DEPLOYMENT_TYPE",length = 4)
    private String deploymentType;

    public String getType() {
        return deploymentType == null ? "DEV" : deploymentType;
    }

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\",\"email\":\"" + email + "\",\"username\":\"" + username + ",\"ipAddress\":\"" + ipAddress + ",\"appName\":\"" + appName + "\",\"paymentUser\":\"" + paymentUser + "\",\"password\":\"" + password + "\"}";
    }
}
