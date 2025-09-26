package com.fcgo.eft.sutra.dto.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealTimeStatusByIdDetail {
    private String id;
    private String instructionId;
    private String creditStatus;
    private String reasonDesc;
    private String recDate;
}
