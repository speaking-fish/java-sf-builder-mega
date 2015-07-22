package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

public class MegaBuilderHelper {
    
    public static final MethodId MethodId_BuiltValues_get       = methodId(BuiltValues.class, "get", Class .class);
    public static final MethodId MethodId_BuiltValues_get_value = methodId(BuiltValues.class, "get", Class .class, Object.class);
    
    public static final MethodId MethodId_Object_equals   = methodId(Object     .class, "equals"  , Object.class);
    public static final MethodId MethodId_Object_hashCode = methodId(Object     .class, "hashCode"              );
    public static final MethodId MethodId_Object_toString = methodId(Object     .class, "toString"              );

    
    public static final MethodInvoker<?, ? extends BuilderInstance<? extends Base, Object, ? extends Base>
    > MethodInvoker_BuiltValues_get = new MethodInvoker<Object, BuilderInstance<? extends Base, Object, ? extends Base>>() {
        @SuppressWarnings("unchecked")
        @Override public Object invoke(BuilderInstance<? extends Base, Object, ? extends Base> instance, Object[] args) {
            return instance.get((Class<GetBase>) args[0]);
        }
    };

    public static final MethodInvoker<?, ? extends BuilderInstance<? extends Base, Object, ? extends Base>
    > MethodInvoker_BuiltValues_get_value = new MethodInvoker<Object, BuilderInstance<? extends Base, Object, ? extends Base>>() {
        @SuppressWarnings("unchecked")
        @Override public Object invoke(BuilderInstance<? extends Base, Object, ? extends Base> instance, Object[] args) {
            return instance.get((Class<GetBase>) args[0], args[1]);
        }
    };

    public static final MethodInvoker<?, ? extends GetterValueInstance<?, ?>
    > MethodInvoker_GetterValue_getter = new MethodInvoker<Object, GetterValueInstance<?, ?>>() {
        @Override public Object invoke(GetterValueInstance<?, ?> instance, Object[] args) {
            return instance.get();
        }
    };
    
    
    public static final MethodInvoker<?, Object
    > MethodInvoker_Object_equals = new MethodInvoker<Object, Object>() {
        @Override public Object invoke(Object instance, Object[] args) {
            return instance.equals(args[0]);
        }
    };
    
    public static final MethodInvoker<?, Object
    > MethodInvoker_Object_hashCode = new MethodInvoker<Object, Object>() {
        @Override public Object invoke(Object instance, Object[] args) {
            return instance.hashCode();
        }
    };
    
    public static final MethodInvoker<?, Object
    > MethodInvoker_Object_toString = new MethodInvoker<Object, Object>() {
        @Override public Object invoke(Object instance, Object[] args) {
            return instance.toString();
        }
    };
    
    public static final CommonInstanceDefinition<?> InstanceDefinition_Object = new CommonInstanceDefinition<Object>() {
        
        {
            _methodById.put(MethodId_Object_equals  , MethodInvoker_Object_equals  );
            _methodById.put(MethodId_Object_hashCode, MethodInvoker_Object_hashCode);
            _methodById.put(MethodId_Object_toString, MethodInvoker_Object_toString);
        }
        
        @Override public ClassLoader instanceClassLoader() {
            throw new UnsupportedOperationException("Unsupported method");
        }

        @Override public Class<?>[] instanceInterfaces() {
            throw new UnsupportedOperationException("Unsupported method");
        }

        @Override public CommonInstanceDefinition<?> parent() { return null; }
        
    };

    public static final CommonInstanceDefinition<?> InstanceDefinition_BuiltValues = new CommonInstanceDefinition<Object>() {
        
        {
            _methodById.put(MethodId_BuiltValues_get      , MethodInvoker_BuiltValues_get      );
            _methodById.put(MethodId_BuiltValues_get_value, MethodInvoker_BuiltValues_get_value);
        }
        
        @Override public ClassLoader instanceClassLoader() {
            throw new UnsupportedOperationException("Unsupported method");
        }

        @Override public Class<?>[] instanceInterfaces() {
            throw new UnsupportedOperationException("Unsupported method");
        }

        @Override public CommonInstanceDefinition<?> parent() { return InstanceDefinition_Object; }
        
    };
    
    public static <T extends Set<Class<?>>> T getInterfaces(T dest, Class<?> src) {
        for(final Class<?> intf : src.getInterfaces()) {
            if(!dest.contains(intf)) {
                dest.add(intf);
                getInterfaces(dest, intf);
            }
        }
        return dest;
    }

    public static HashSet<Class<?>> getInterfaces(Class<?> src) {
        return getInterfaces(new HashSet<Class<?>>(), src);
    }
    
    public static Method[] getMethodsDeclaredAfter(Class<?> intf, Class<?> stop) {
        Method[] result = new Method[0];
        outer: while(true) {
            if(stop.isAssignableFrom(intf)) {
                Method[] temp = intf.getDeclaredMethods(); 
                if(0 != temp.length) {
                    result = temp;
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
    
    public static boolean inheritedFrom(Class<?> estimatedChild, Class<?> estimatedParent) {
        return !estimatedParent.equals(estimatedChild) && estimatedParent.isAssignableFrom(estimatedChild);
    }

}
