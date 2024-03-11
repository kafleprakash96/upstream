package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto {
    private Long id;
    private String clientName;
    private String clientCode;
    private boolean standalone;
}
