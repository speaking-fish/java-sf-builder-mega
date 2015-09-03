package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.InvocationHandler;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

public class TransValueInstance<TRANSITION extends TransBase, GETTER extends GetBase, VALUE_TYPE
> extends CommonInstance<
    TransValueDefinition<TRANSITION, GETTER, VALUE_TYPE>,
    TransValueInstance  <TRANSITION, GETTER, VALUE_TYPE>,
    TRANSITION
> implements InvocationHandler {
    
    protected boolean _assigned;
    protected final VALUE_TYPE _value;
    
    public TransValueInstance(
        TransValueDefinition<TRANSITION, GETTER, VALUE_TYPE> definition,
        boolean                                              assigned  ,
        VALUE_TYPE                                           value
    ) {
        super(definition);
        _assigned = assigned;
        _value    = value   ;
    }

    public GETTER transition(VALUE_TYPE value) {
        return definition().getterDefinition().create(_assigned ? _value : value);
    }
    
}
