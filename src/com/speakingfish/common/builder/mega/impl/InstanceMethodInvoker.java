package com.speakingfish.common.builder.mega.impl;

import com.speakingfish.common.builder.mega.MegaBuilder.Base;

/**
 * 
 * Internal interface - builder method
 *
 * @param <RESULT>
 */
public interface InstanceMethodInvoker<RESULT, BUILDER extends Base, RESULT_CLASS, INITIAL_BUILDER extends Base> {
    RESULT invoke(Instance<BUILDER, RESULT_CLASS, INITIAL_BUILDER> instance, Object[] args);
}