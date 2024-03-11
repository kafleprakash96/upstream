package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateDto {

    private Long id;
    private String stateName;
    private String stateCode;
}
