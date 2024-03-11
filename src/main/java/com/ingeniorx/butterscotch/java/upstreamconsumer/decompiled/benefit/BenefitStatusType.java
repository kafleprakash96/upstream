package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.benefit;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
public enum BenefitStatusType {
    SUBMITTED_PBM("SUBMITTED_PBM"),
    REJECTED("REJECTED"),
    INCOMPLETE("INCOMPLETE"),
    IN_REMEDIATION("IN_REMEDIATION"),
    IN_PROGRESS("IN_PROGRESS"),
    CLIENT_APPROVAL("CLIENT_APPROVAL"),
    BULK_PREPRODUCTION("BULK_PREPRODUCTION"),
    PBM_ACKNOWLEDGED ("PBM_ACKNOWLEDGED"),
    //added the status for internal down stream
    SUBMITTED_INTERNAL_DOWNSTREAM("SUBMITTED_INTERNAL_DOWNSTREAM"),

    ERR("ERR"),

    PENDED("PENDED"),

    UNPENDED("UNPENDED"),

    TEST("TEST"),

    CANCELLED("CANCELLED"),

    COMPLETE("COMPLETE"),

    OBSOLETE("OBSOLETE");

    private String type;
    public static final Set<String> allValues = Arrays.stream(values())
            .map(BenefitStatusType::getType)
            .collect(Collectors.toUnmodifiableSet());

    BenefitStatusType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
    }

