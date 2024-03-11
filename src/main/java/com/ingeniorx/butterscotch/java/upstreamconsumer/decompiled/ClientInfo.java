package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ClientInfo {
    String idSubField() default " ";
}
