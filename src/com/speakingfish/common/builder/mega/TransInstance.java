package com.speakingfish.common.builder.mega;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

/**
 * 
 * Internal class - transition instance
 *
 */
class TransInstance {
    
    public final Instance                 parent     ;
    public final Class<? extends GetBase> getterClass;
    public final Object                   value      ;
    
    public TransInstance(
        Instance                  parent     ,
        Class<?  extends GetBase> getterClass,
        Object                    value
    ) {
        super();
        this.parent      = parent     ;
        this.getterClass = getterClass;
        this.value       = value      ;
    }
}