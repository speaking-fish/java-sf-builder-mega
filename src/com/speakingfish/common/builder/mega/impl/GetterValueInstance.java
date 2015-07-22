package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.InvocationHandler;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

public class GetterValueInstance<GETTER extends GetBase, VALUE_TYPE
> extends CommonInstance<
    GetterValueDefinition<GETTER, VALUE_TYPE>,
    GetterValueInstance  <GETTER, VALUE_TYPE>,
    GETTER
> implements InvocationHandler {
    
    protected final VALUE_TYPE _value;
    
    public GetterValueInstance(
        GetterValueDefinition<GETTER, VALUE_TYPE> definition, VALUE_TYPE value
    ) {
        super(definition);
        _value = value;
    }

    public VALUE_TYPE get() { return _value; }
    
}
