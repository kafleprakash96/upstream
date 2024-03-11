package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FileStatusDto {
    private Long id;
    private String fileName;
    private FileStatusType status;
    private String client;
    @ClientInfo
    private Long clientId;
    private LocalDateTime dateReceived;
    private Integer totalBenefits;
    private Integer inRemediation;
    private Integer clientApproval;
    private Integer submittedToPbm;
    private String transactionId;
    private String errorDescription;
}
