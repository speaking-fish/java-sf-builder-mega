package com.speakingfish.common.builder.mega.impl;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

/**
 * 
 * Internal class - transition instance
 *
 */
public class TransInstance<PARENT_BUILDER extends Base, RESULT_CLASS, INITIAL_BUILDER extends Base> {
    
    public final Instance<PARENT_BUILDER, RESULT_CLASS, INITIAL_BUILDER> parent     ;
    public final Class   <? extends GetBase                            > getterClass;
    public final Object                                                  value      ;
    
    public TransInstance(
        Instance<PARENT_BUILDER, RESULT_CLASS, INITIAL_BUILDER> parent     ,
        Class   <?  extends GetBase                           > getterClass,
        Object                                                  value
    ) {
        super();
        this.parent      = parent     ;
        this.getterClass = getterClass;
        this.value       = value      ;
    }
    
}