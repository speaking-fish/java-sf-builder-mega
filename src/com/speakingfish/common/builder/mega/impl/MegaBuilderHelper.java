package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.Method;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

public class MegaBuilderHelper {
    
    public static Method[] getMethodsDeclaredAfter(Class<?> intf, Class<?> stop) {
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

    public static <T, R extends T> Class<R> getInterfaceDeclaredAfter(Class<R> intf, Class<T> stop) {
        while(true) {
            if(!stop.isAssignableFrom(intf)) {
                return null;
            }
            Class<?>[] interfaces = intf.getInterfaces();
            for(Class<?> superIntf : interfaces) {
                if(stop.isAssignableFrom(superIntf)) {
                    @SuppressWarnings("unchecked")
                    Class<R> result = getInterfaceDeclaredAfter((Class<R>) superIntf, stop);
                    //Class<T> result = getInterfaceDeclaredAfter(superIntf, stop);
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

    public static MethodId methodId(Class<?> origin, String name, Class<?>... parameterTypes) {
        try {
            return new MethodId(origin.getMethod(name, parameterTypes));
        } catch(RuntimeException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    public static final InstanceMethodInvoker<
        Object, ? extends Base, Object, ? extends Base
    > InstanceMethodInvoker_BuiltValues_get = new InstanceMethodInvoker<Object, Base, Object, Base>() {
        @SuppressWarnings("unchecked")
        @Override public Object invoke(Instance<Base, Object, Base> instance, Object[] args) {
            return instance.get((Class<GetBase>) args[0]);
        }
    };
    
    public static final InstanceMethodInvoker<
        Object, ? extends Base, Object, ? extends Base
    > InstanceMethodInvoker_Object_equals = new InstanceMethodInvoker<Object, Base, Object, Base>() {
        @Override public Object invoke(Instance<Base, Object, Base>  instance, Object[] args) {
            return instance.equals(args[0]);
        }
    };
    
    public static final InstanceMethodInvoker<
        Object, ? extends Base, Object, ? extends Base
    > InstanceMethodInvoker_Object_hashCode = new InstanceMethodInvoker<Object, Base, Object, Base>() {
        @Override public Object invoke(Instance<Base, Object, Base>  instance, Object[] args) {
            return instance.hashCode();
        }
    };
    
    public static final InstanceMethodInvoker<
        Object, ? extends Base, Object, ? extends Base
    > InstanceMethodInvoker_Object_toString = new InstanceMethodInvoker<Object, Base, Object, Base>() {
        @Override public Object invoke(Instance<Base, Object, Base> instance, Object[] args) {
            return instance.toString();
        }
    };
    
    public static final MethodId MethodId_BuiltValues_get = methodId(BuiltValues.class, "get"     , Class .class);
    
    public static final MethodId MethodId_Object_equals   = methodId(Object     .class, "equals"  , Object.class);
    public static final MethodId MethodId_Object_hashCode = methodId(Object     .class, "hashCode"              );
    public static final MethodId MethodId_Object_toString = methodId(Object     .class, "toString"              );

}
