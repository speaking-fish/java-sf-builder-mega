package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 
 * Method Id
 *
 */
public class MethodId implements Comparable<MethodId> {
    
    public final String     name          ;
    public final Class<?>[] parameterTypes;
    
    protected final int _hashCode;
    
    public MethodId(String name, Class<?>[] parameterTypes) {
        super();
        this.name           = name          ;
        this.parameterTypes = parameterTypes;
        _hashCode = name.hashCode() * 31 + Arrays.hashCode(parameterTypes);
    }
    
    public MethodId(Method method) {
        this(method.getName(), method.getParameterTypes());
    }
    
    @Override public int hashCode() { return _hashCode; }

    @Override public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        MethodId other = (MethodId) obj;
        return true
            && name.equals(other.name)
            && Arrays.equals(parameterTypes, other.parameterTypes)
            ;
    }

    @Override public int compareTo(MethodId other) {
        int result;
        result = name.compareTo(other.name);
        if(result != 0) return result;
        for(int i = 0, count = Math.min(parameterTypes.length, other.parameterTypes.length); i < count; ++i) {
            result = parameterTypes[i].getName().compareTo(other.parameterTypes[i].getName());
            if(result != 0) return result;
        }
        return other.parameterTypes.length - parameterTypes.length;
    }
    
}