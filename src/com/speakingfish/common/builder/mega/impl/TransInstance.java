package com.speakingfish.common.builder.mega.impl;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

/**
 * 
 * Internal class - transition instance
 *
 */
public class TransInstance<CONTEXT, PARENT_BUILDER extends Base, RESULT_CLASS, INITIAL_BUILDER extends Base> {
    
    public final BuilderInstance<CONTEXT, PARENT_BUILDER, RESULT_CLASS, INITIAL_BUILDER> parent     ;
    public final Class          <? extends GetBase                                     > getterClass;
    public final Class          <? extends TransBase                                   > transClass ;
    public final Object                                                                  value      ;
    
    public TransInstance(
        BuilderInstance<CONTEXT, PARENT_BUILDER, RESULT_CLASS, INITIAL_BUILDER> parent     ,
        Class          <? extends GetBase                                     > getterClass,
        Class          <? extends TransBase                                   > transClass ,
        Object                                                                  value
    ) {
        super();
        this.parent      = parent     ;
        this.getterClass = getterClass;
        this.transClass  = transClass ;
        this.value       = value      ;
    }
    
}