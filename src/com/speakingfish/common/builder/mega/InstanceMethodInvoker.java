package com.speakingfish.common.builder.mega;

interface InstanceMethodInvoker<RESULT> {
    RESULT invoke(Instance instance, Object[] args);
}