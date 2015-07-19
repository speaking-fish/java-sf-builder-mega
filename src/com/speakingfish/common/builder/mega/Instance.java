package com.speakingfish.common.builder.mega;

import java.lang.reflect.InvocationHandler;

interface Instance extends BuiltValues, InvocationHandler {
    Object getValue(Class<?> key);
    Object proxy();
}