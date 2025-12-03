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

    @Override
    public String toString() {
        return "{\"instructionId\":" + instructionId + ",\"pushed\":\"" + pushed + "\"}";
    }
}
