package com.speakingfish.common.builder.mega;


/** Internal interface */
public interface BuiltValues extends Base {
  //Map<Class<GetBase>, GetBase> buildValues();
    <T extends GetBase> T get(Class<T> key);
}