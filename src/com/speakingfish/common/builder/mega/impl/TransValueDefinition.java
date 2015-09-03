package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.Method;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

import static com.speakingfish.common.builder.mega.impl.MegaBuilderHelper.*; 

public abstract class TransValueDefinition<TRANSITION extends TransBase, GETTER extends GetBase, VALUE_TYPE>
extends CommonInstanceDefinition<TransValueInstance<TRANSITION, GETTER, VALUE_TYPE>> {

    //protected final GetterValueDefinition<GETTER, VALUE_TYPE> _getterDefinition;
    //protected final Class<GETTER> _getterClass;
    
    public TransValueDefinition(
        //GetterValueDefinition<GETTER, VALUE_TYPE> getterDefinition,
        //Class<GETTER> getterClass,
        Method transitionClassMethod
    ) {
        super();
        //_getterDefinition = getterDefinition;
        _methodById.put(new MethodId(transitionClassMethod), MethodInvoker_TransValue_transition);
    }

    @Override public CommonInstanceDefinition<?> parent() { return InstanceDefinition_Object; }
    
    public abstract GetterValueDefinition<GETTER, VALUE_TYPE> getterDefinition();// { return _getterDefinition; }
    
    public TransValueInstance<TRANSITION, GETTER, VALUE_TYPE> createInstance(boolean assigned, final VALUE_TYPE value) {
        return new TransValueInstance<TRANSITION, GETTER, VALUE_TYPE>(this, assigned, value);
    }
    
    public TRANSITION create(boolean assigned, final VALUE_TYPE value) {
        return createInstance(assigned, value).proxy();
    }
    
}
