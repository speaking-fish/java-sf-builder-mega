package com.speakingfish.common.builder.mega;

public interface ClassBuilder<T> {
    
    T build(BuiltValues values);
    
}
