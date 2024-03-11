package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicLayerDto {

    private Long id;
    private Set<ClientDto> clients;
    private Set<LobDto> lobs;
    private Set<StateDto> states;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate effectiveDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate terminationDate;

    private String layerName;
    private String layerDescription;
    private String standardOutputValue;

    private Boolean isRequired;
    private DynamicLayerCategoriesDto dynamicLayerCategory;
}
