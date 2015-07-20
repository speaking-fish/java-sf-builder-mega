package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.InvocationHandler;

import com.speakingfish.common.builder.mega.MegaBuilder;
import com.speakingfish.common.builder.mega.MegaBuilder.*;

/**
 *
 * Internal interface - builder implementation
 * 
 */
public interface Instance<BUILDER extends Base, RESULT_CLASS, INITIAL_BUILDER extends Base> extends BuiltValues, InvocationHandler {
    Object getValue(Class<?> key);
    
    BUILDER                                    proxy  ();
    MegaBuilder<RESULT_CLASS, INITIAL_BUILDER> builder();
}