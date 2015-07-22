package com.speakingfish.common.builder.mega.impl;

public interface MethodInvoker<RESULT, INSTANCE> {
    RESULT invoke(INSTANCE instance, Object[] args);
}
