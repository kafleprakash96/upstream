package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicLayerCategoriesDto {

    private Long id;
    private String categoryName;
}
