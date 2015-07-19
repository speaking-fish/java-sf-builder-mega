package com.speakingfish.common.builder.mega;

import java.lang.reflect.InvocationHandler;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

/**
 *
 * Internal interface - builder implementation
 * 
 */
interface Instance extends BuiltValues, InvocationHandler {
    Object getValue(Class<?> key);
    Object proxy();
}