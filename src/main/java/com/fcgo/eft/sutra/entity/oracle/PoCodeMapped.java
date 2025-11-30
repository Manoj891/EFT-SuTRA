package com.fcgo.eft.sutra.entity.oracle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PO_CODE_MAPPED")
public class PoCodeMapped {
    @Id
    @Column(name = "PO_CODE", length = 15)
    private Long poCode;

    @Column(name = "CODE", length = 4)
    private Integer code;
}
