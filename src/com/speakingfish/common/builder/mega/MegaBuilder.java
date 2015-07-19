package com.speakingfish.common.builder.mega;

import java.lang.reflect.Method;

public class MegaBuilder {
    
    protected static Method[] getMethodsDeclaredAfter(Class<?> intf, Class<?> stop) {
        Method[] result = new Method[0];
        outer: while(true) {
            if(stop.isAssignableFrom(intf)) {
                if(0 != intf.getDeclaredMethods().length) {
                    result = intf.getDeclaredMethods();
                }
            }
            Class<?>[] interfaces = intf.getInterfaces();
            for(Class<?> superIntf : interfaces) {
                if(stop.isAssignableFrom(superIntf)) {
                    intf = superIntf;
                    continue outer;
                }
            }
            break;
        }
        return result;
    }

    protected static <T, R extends T> Class<R> getIntfDeclaredAfter(Class<R> intf, Class<T> stop) {
        while(true) {
            if(!stop.isAssignableFrom(intf)) {
                return null;
            }
            Class<?>[] interfaces = intf.getInterfaces();
            for(Class<?> superIntf : interfaces) {
                if(stop.isAssignableFrom(superIntf)) {
                    Class<R> result = getIntfDeclaredAfter((Class<R>) superIntf, stop);
                    if(null != result) {
                        return result;
                    } else {
                        return intf;
                    }
                }
            }
            break;
        }
        return null;
    }
    
    protected static class MethodInvokerBuiltValues implements MethodInvoker<Object> {
        
        public static MethodInvokerBuiltValues INSTANCE = new MethodInvokerBuiltValues();
        
        @SuppressWarnings("unchecked")
        @Override public Object invoke(Instance instance, Object[] args) {
            return instance.get((Class<GetBase>) args[0]);
        }
        
    }

    protected static class MethodInvokerEquals implements MethodInvoker<Object> {
        
        public static MethodInvokerEquals INSTANCE = new MethodInvokerEquals();
        
        @Override public Object invoke(Instance instance, Object[] args) {
            return instance.equals(args[0]);
        }
        
    }

    protected static class MethodInvokerHashCode implements MethodInvoker<Object> {
        
        public static MethodInvokerHashCode INSTANCE = new MethodInvokerHashCode();
        
        @Override public Object invoke(Instance instance, Object[] args) {
            return instance.hashCode();
        }
        
    }

}
