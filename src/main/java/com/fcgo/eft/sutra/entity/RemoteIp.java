package com.fcgo.eft.sutra.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "REMOTE_IP")
public class RemoteIp {
    @Id
    @Column(name = "ID", length = 36)
    private String id;
    @Column(name = "IP", length = 20)
    private String ip;
    @Column(name = "USERNAME", length = 20)
    private String username;
    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        lastLogin = LocalDateTime.now();
    }
}
