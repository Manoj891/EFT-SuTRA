package com.fcgo.eft.sutra.entity.mssql;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "acc_epayment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccEpayment {
    @Id
    @Column(name = "eftno")
    private Long eftNo;


    @Column(name = "pstatus", nullable = false)
    private Integer pStatus;


    @Column(name = "StatusMessage", length = 500)
    private String statusMessage;


    @Column(name = "successSN", nullable = false)
    private Long successSn;

}
