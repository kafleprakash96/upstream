package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContractCodeDto {
    private Long id;
    private String contractCode;
    private String benefitPlanId;
    private String sourceSystem;
    private String sourceData;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") private LocalDate contractEffDate;
    private Long benefitPlanIdVersion;
    @JsonDeserialize(using = LocalDateDeserializer.class) @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") private LocalDate benefitPlanEffDate;
    @JsonDeserialize(using = LocalDateDeserializer.class) @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") private LocalDate contractTermDate;
    private boolean sendDownstream;
    private String uniquePlanBenefitVersion;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize (using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedTimeAndDate;
    private String responseDescription;
    private String responseCode;
    private String planType;
    private Boolean cepFlag;
    private String benefitVersion;
    private String soFileName;
    private String userId;
}
