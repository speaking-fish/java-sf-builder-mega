package com.speakingfish.common.builder.mega.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class CommonInstanceDefinition<INSTANCE> {
    
    protected final Map<MethodId, MethodInvoker<?, ?>> _methodById = new HashMap<MethodId, MethodInvoker<?, ?>>();
    
    public CommonInstanceDefinition() {
        super();
    }
    
    public abstract ClassLoader instanceClassLoader();
    public abstract Class<?>[] instanceInterfaces();
    public abstract CommonInstanceDefinition<?> parent();
    
    @SuppressWarnings("unchecked")
    public MethodInvoker<?, INSTANCE> methodById(MethodId id) {
        final MethodInvoker<?, INSTANCE> result = (MethodInvoker<?, INSTANCE>) _methodById.get(id);
        if(null != result) {
            return result;
        } else if(null != parent()) {
            return (MethodInvoker<?, INSTANCE>) parent().methodById(id);
        } else {
            return null;
        }
    }

    @Override public String toString() {
        return "Definition: " + getClass().getName() + " interfaces: " + Arrays.toString(instanceInterfaces());
    }
    
}
