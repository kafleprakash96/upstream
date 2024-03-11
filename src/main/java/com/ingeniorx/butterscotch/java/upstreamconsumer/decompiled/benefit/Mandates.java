package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.benefit;

public enum Mandates {

    APPLY_ALL("Apply-all"),
    OPT_IN("Opt-In");

    private final String name;

    Mandates(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
