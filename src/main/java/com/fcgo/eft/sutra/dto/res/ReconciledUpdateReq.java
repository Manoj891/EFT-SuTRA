package com.fcgo.eft.sutra.dto.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReconciledUpdateReq {
    private long instructionId;
    private String pushed;
}
