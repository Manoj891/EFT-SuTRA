package com.fcgo.eft.sutra.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "NCHL_TOKEN")
public class NchlToken {
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "ACCESS_TOKEN")
    private String access_token;
    @Column(name = "TOKEN_TYPE")
    private String token_type;
    @Column(name = "REFRESH_TOKEN")
    private String refresh_token;
    @Column(name = "SCOPE")
    private String scope;
    @Column(name = "EXPIRES_IN")
    private Integer expires_in;
    @Column(name = "EXPIRED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;

    public Integer getExpiresIn() {
        return expires_in;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    @Override
    public String toString() {
        return "{\"access_token\":\"" + access_token + "\",\"token_type\":\"" + token_type + "\",\"refresh_token\":\"" + refresh_token + "\",\"scope\":\"" + scope + "\",\"scope\":\"" + scope + "\",\"expiresIn\":\"" + expires_in + "\",\"expiredAt\":\"" + expiredAt + "\"}";
    }

}
