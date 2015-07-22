package com.speakingfish.common.builder.mega.impl;

import com.speakingfish.common.builder.mega.MegaBuilder;
import com.speakingfish.common.builder.mega.MegaBuilderDefinition;
import com.speakingfish.common.builder.mega.MegaBuilder.*;

public class BuilderInstance<BUILDER extends Base, RESULT_CLASS, INITIAL_BUILDER extends Base>
extends CommonInstance<
    MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>.Definition<BUILDER>,
    BuilderInstance<BUILDER, RESULT_CLASS, INITIAL_BUILDER>,
    BUILDER
> implements BuiltValues {
    protected final MegaBuilder  <                RESULT_CLASS, INITIAL_BUILDER> _builder   ;
    protected final TransInstance<? extends Base, RESULT_CLASS, INITIAL_BUILDER> _transition;
    
    public BuilderInstance(
        MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>.Definition<BUILDER> definition,
        MegaBuilder          <                RESULT_CLASS, INITIAL_BUILDER    > builder   ,
        TransInstance        <? extends Base, RESULT_CLASS, INITIAL_BUILDER    > transition
    ) {
        super(definition);
        _builder    = builder   ;
        _transition = transition;
    }
    
    public MegaBuilder  <                RESULT_CLASS, INITIAL_BUILDER> builder   () { return _builder   ; }
    public TransInstance<? extends Base, RESULT_CLASS, INITIAL_BUILDER> transition() { return _transition; }
    
    @SuppressWarnings("unchecked")
    @Override public <T extends GetBase> T get(Class<T> key) {
        if(key.isInstance(_proxy)) {
            return (T) _proxy;
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override public <T extends GetBase> T get(Class<T> key, Object defaultValue) {
        if(key.isInstance(_proxy)) {
            return (T) _proxy;
        } else {
            return definition().createGetterValue(key, defaultValue);
        }
    }
    
    public Object getValue(final Class<?> key) {
        {}     if(null == key) {
            throw new NullPointerException("Key is null");
        } else if(null == _transition) {
            throw new IllegalArgumentException("Unsupported class key: " + key.getName());
        } else if(key == _transition.getterClass) {
            return _transition.value;
        } else if(null == _transition.parent) {
            throw new IllegalArgumentException("Unsupported class key: " + key.getName());
        } else {
            return _transition.parent.getValue(key);
        }
    }

    
}
