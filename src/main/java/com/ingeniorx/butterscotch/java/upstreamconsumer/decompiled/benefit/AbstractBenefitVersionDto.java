package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.benefit;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public abstract class AbstractBenefitVersionDto {
    private Long id;
    private Long benefitId;
    private String version;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime versionCreatedDate;
    private LocalDateTime productionDate;
    private Boolean retro;
    private Long retroVersionId;
    private BenefitStatusType benefitStatus;
    private String uniquePlanBenefitVersion;
    private String projectId;
    private List<BenefitSystematicKeywordDto> benefitSystematicKeywords;
    private LocalDateTime lastUpdated;
    private String retroVersion;
}
