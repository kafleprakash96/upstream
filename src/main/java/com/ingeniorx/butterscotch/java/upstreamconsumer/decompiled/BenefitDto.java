package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.benefit.LightBenefitVersionDto;
import com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.benefit.Mandates;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedSet;

@Data
public class BenefitDto {

    private Long id;
    @ClientInfo(idSubField = "id")
    private StateDto state;
    private LobDto lob;
    private ClientDto client;
    private String benefitPlanId;
    private String benefitCode;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;
    private List<DynamicLayerDto> dynamicLayers;
//    private BenefitVersionDto benefitVersion;
    private SortedSet<LightBenefitVersionDto> lightBenefitVersions;
    private String copy0f;
    private Mandates mandates;
    private String projectId;
    private String benefitLocked;
    private String copiedBenefitPlanId;
    private Boolean updateIndicator;
    private Long latestVersionId;
    private String latestVersionStatus;
    private Boolean latestVersionLockedStatus;
}