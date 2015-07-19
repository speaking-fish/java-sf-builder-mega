package com.speakingfish.common.builder.mega;

/**
 * 
 * Internal interface - builder method
 *
 * @param <RESULT>
 */
interface InstanceMethodInvoker<RESULT> {
    RESULT invoke(Instance instance, Object[] args);
}