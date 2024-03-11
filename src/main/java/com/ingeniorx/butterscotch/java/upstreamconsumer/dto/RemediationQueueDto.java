package com.ingeniorx.butterscotch.java.upstreamconsumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemediationQueueDto {
    private Long id;
    private String filename;
    private String is_cep_flag;
}
