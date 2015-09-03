package com.speakingfish.common.builder.mega.impl;

import com.speakingfish.common.builder.mega.MegaBuilder;
import com.speakingfish.common.builder.mega.MegaBuilder.Base;
import com.speakingfish.common.builder.mega.MegaBuilder.GetBase;
import com.speakingfish.common.builder.mega.MegaBuilder.TransBase;
import com.speakingfish.common.builder.mega.MegaBuilderDefinition;
import com.speakingfish.common.builder.mega.MegaBuilder.*;

public class BuilderInstance<CONTEXT, BUILDER extends Base, RESULT_CLASS, INITIAL_BUILDER extends Base>
extends CommonInstance<
    MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>.Definition<CONTEXT, BUILDER>,
    BuilderInstance<CONTEXT, BUILDER, RESULT_CLASS, INITIAL_BUILDER>,
    BUILDER
> implements BuiltValues {
    protected final MegaBuilder  <CONTEXT,                 RESULT_CLASS, INITIAL_BUILDER>.Instance _builder   ;
    protected final TransInstance<CONTEXT, ? extends Base, RESULT_CLASS, INITIAL_BUILDER>          _transition;
    
    public BuilderInstance(
        MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>.Definition<CONTEXT, BUILDER>          definition,
        MegaBuilder          <CONTEXT,                 RESULT_CLASS, INITIAL_BUILDER    >.Instance builder   ,
        TransInstance        <CONTEXT, ? extends Base, RESULT_CLASS, INITIAL_BUILDER    >          transition
    ) {
        super(definition);
        _builder    = builder   ;
        _transition = transition;
    }
    
    public MegaBuilder  <CONTEXT,                 RESULT_CLASS, INITIAL_BUILDER>.Instance builder   () { return _builder   ; }
    public TransInstance<CONTEXT, ? extends Base, RESULT_CLASS, INITIAL_BUILDER>          transition() { return _transition; }
    
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
    
    @SuppressWarnings("unchecked")
    @Override public <T extends TransBase> T getDefault(Class<? super T> key) {
        final TransInstance<CONTEXT, ? extends Base, RESULT_CLASS, Base> transInstance = getTransInstance((Class<T>) key);
        return definition().createTransValue((Class<T>) key, transInstance);
    }

    @SuppressWarnings("unchecked")
    public <T extends TransBase> TransInstance<CONTEXT, ? extends Base, RESULT_CLASS, Base> getTransInstance(Class<T> key) {
        {}     if(null == key) {
            throw new NullPointerException("Key is null");
        } else if(null == _transition) {
            return null;
        } else if(key == _transition.transClass) {
            return (TransInstance<CONTEXT, ? extends Base, RESULT_CLASS, Base>) _transition;
        } else if(null == _transition.parent) {
            return null;
        } else {
            return _transition.parent.getTransInstance(key);
        }
    }
    
    public Object getValue(final Class<? extends GetBase> key) {
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

    @Override public boolean hasValue(Class<? extends GetBase> key) {
        {}     if(null == key) {
            throw new NullPointerException("Key is null");
        } else if(null == _transition) {
            return false;
        } else if(key == _transition.getterClass) {
            return true;
        } else if(null == _transition.parent) {
            return false;
        } else {
            return _transition.parent.hasValue(key);
        }
    }

    
}
