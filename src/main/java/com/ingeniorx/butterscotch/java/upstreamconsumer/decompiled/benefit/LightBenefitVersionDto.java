package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.benefit;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
public class LightBenefitVersionDto extends AbstractBenefitVersionDto implements Comparable<AbstractBenefitVersionDto> {
    public LightBenefitVersionDto(AbstractBenefitVersionDto abstractBenefitVersionDto) {
        this.setId(abstractBenefitVersionDto.getId());
        this.setBenefitId(abstractBenefitVersionDto.getBenefitId());
        this.setVersion(abstractBenefitVersionDto.getVersion());
        this.setStartDate(abstractBenefitVersionDto.getStartDate());
        this.setEndDate(abstractBenefitVersionDto.getEndDate());
        this.setVersionCreatedDate(abstractBenefitVersionDto.getVersionCreatedDate());
        this.setProductionDate(abstractBenefitVersionDto.getProductionDate());
        this.setRetro(abstractBenefitVersionDto.getRetro());
        this.setRetroVersionId(abstractBenefitVersionDto.getRetroVersionId());
        this.setBenefitStatus(abstractBenefitVersionDto.getBenefitStatus());
        this.setUniquePlanBenefitVersion(abstractBenefitVersionDto.getUniquePlanBenefitVersion());
        this.setProjectId(abstractBenefitVersionDto.getProjectId());
        this.setLastUpdated(abstractBenefitVersionDto.getLastUpdated());
    }
    @Override
    public int compareTo(@NonNull AbstractBenefitVersionDto benefitVersion) {
        Integer versionMajor = Integer.parseInt(benefitVersion.getVersion().split("\\.")[0]);
        Integer versionMinor = Integer.parseInt(benefitVersion.getVersion().split("\\.")[1]);
        Integer currentMajor = Integer.parseInt(this.getVersion().split("\\.")[0]);
        Integer currentMinor = Integer.parseInt(this.getVersion().split("\\.")[1]);
        return versionMajor.equals(currentMajor) ? versionMinor.compareTo(currentMinor) : versionMajor.compareTo(currentMajor);
    }
}