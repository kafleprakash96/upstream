package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum FileStatusType {
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE"),
    COMPLETED_WITH_ERRORS("COMPLETED_WITH_ERRORS"),
    REJECTED("REJECTED"),
    INVALID_FILE("INVALID_FILE"),
    INVALID_EMPTY("INVALID_EMPTY"),
    INVALID_NON_PHARM("INVALID_NON_PHARM"),
    INVALID_MAPPING("INVALID_MAPPING"),
    MISSING_CCRS_LGC("MISSING_CCRS_LGC"),
    MISSING_DYNAMIC_LAYER("MISSING_DYNAMIC_LAYER"),
    CONTRACT_ADDITION("CONTRACT_ADDITION"),
    CONTRACT_UPDATE("CONTRACT_UPDATE");


    private String type;

    public static final Set<String> allValues = Arrays.stream(values())
            .map(FileStatusType::getType)
            .collect(Collectors.toUnmodifiableSet());

    FileStatusType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
