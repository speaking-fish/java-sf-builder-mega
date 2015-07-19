package com.speakingfish.common.builder.mega;

interface MethodInvoker<RESULT> {
    RESULT invoke(Instance instance, Object[] args);
}