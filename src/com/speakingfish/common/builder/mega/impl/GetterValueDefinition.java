package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.Method;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

import static com.speakingfish.common.builder.mega.impl.MegaBuilderHelper.*; 

public abstract class GetterValueDefinition<GETTER extends GetBase, VALUE_TYPE> extends CommonInstanceDefinition<GetterValueInstance<GETTER, VALUE_TYPE>> {

    public GetterValueDefinition(Method getterClassMethod) {
        super();
        
        _methodById.put(new MethodId(getterClassMethod), MethodInvoker_GetterValue_getter);
    }

    @Override public CommonInstanceDefinition<?> parent() { return InstanceDefinition_Object; }
    
    public GetterValueInstance<GETTER, VALUE_TYPE> createInstance(final VALUE_TYPE value) {
        return new GetterValueInstance<GETTER, VALUE_TYPE>(this, value);
    }
    
    public GETTER create(final VALUE_TYPE value) {
        return createInstance(value).proxy();
    }
    
}
