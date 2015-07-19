package com.speakingfish.common.builder.mega;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

import static com.speakingfish.common.builder.mega.MegaBuilder.*;

public class MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER extends Base> {
    
    protected final Class      <RESULT_CLASS             >  _resultClass             ;
    protected final Constructor<RESULT_CLASS             >  _resultClassConstructor  ;
    protected final Definition <INITIAL_BUILDER          >  _initialBuilderDefinition;
    protected final Map        <Class     <? extends Base>,
                                Definition<? extends Base>> _definitionByClass       ;
    
    protected class MethodInvokerBuilt implements MethodInvoker<RESULT_CLASS> {
        @Override public RESULT_CLASS invoke(Instance instance, Object[] args) {
            try {
                return _resultClassConstructor.newInstance(instance);
            } catch(ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public final MethodInvokerBuilt MethodInvokerBuilt_INSTANCE = new MethodInvokerBuilt();

    public MegaBuilderDefinition(Class<RESULT_CLASS> resultClass, Class<INITIAL_BUILDER> initialBuilderClass) {
        _resultClass = resultClass;
        _definitionByClass = new HashMap<Class<? extends Base>, Definition<? extends Base>>();
        try {
            _resultClassConstructor = _resultClass.getDeclaredConstructor(BuiltValues.class);
            _resultClassConstructor.setAccessible(true);
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        _initialBuilderDefinition = getDefinition(initialBuilderClass);
    }
    
    protected <BUILDER extends Base> Definition<BUILDER> getDefinition(Class<BUILDER> builderClass) {
        @SuppressWarnings("unchecked")
        Definition<BUILDER> result = (Definition<BUILDER>) _definitionByClass.get(builderClass);
        if(null == result) {
            result = new Definition<BUILDER>(builderClass);
            _definitionByClass.put(builderClass, result);
            // put reference to initialized class before initialize to allow resolving references to this class in initialization
            result.init();
        }
        return result;
    }

    protected static <T extends Set<Class<?>>> T getInterfaces(T dest, Class<?> src) {
        for(Class<?> intf : src.getInterfaces()) {
            if(!dest.contains(intf)) {
                dest.add(intf);
                getInterfaces(dest, intf);
            }
        }
        return dest;
    }

    protected static HashSet<Class<?>> getInterfaces(Class<?> src) {
        return getInterfaces(new HashSet<Class<?>>(), src);
    }
    
        
    /**
     * 
     */
    protected class Definition<BUILDER extends Base> {
        
        protected final Class<BUILDER> _builderClass;
        protected final Map<MethodIdentifier, MethodInvoker<?>> _methodById;
        
        /**
         * 
         * RESULT get[NAME]()
         *
         * @param <RESULT>
         */
        protected class MethodInvokerGet<RESULT> implements MethodInvoker<RESULT> {
            
            protected final Class<? extends GetBase> _getIntf;
    
            public MethodInvokerGet(Class<? extends GetBase> getIntf) {
                _getIntf = getIntf;
            }
    
            @SuppressWarnings("unchecked")
            @Override public RESULT invoke(Instance instance, Object[] args) {
                return (RESULT) instance.getValue(_getIntf);
            }
            
        } 
    
        /**
         * 
         * <TYPE extends TransBase> [NAME](VALUE value)
         *
         * @param <RESULT>
         * @param <ARG>
         * @param <RESULTCLASS>
         */
        protected class MethodInvokerTransition<RESULT extends Base, ARG> implements MethodInvoker<RESULT> {
            
            protected final Definition<RESULT           > _resultDefinition;
            protected final Class     <? extends GetBase> _getterClass     ;
    
            public MethodInvokerTransition(
                Definition<RESULT           > resultDefinition,
                Class     <? extends GetBase> getterClass
            ) {
                _resultDefinition = resultDefinition;
                _getterClass      = getterClass     ;
            }
    
            @SuppressWarnings("unchecked")
            @Override public RESULT invoke(Instance instance, Object[] args) {
                return _resultDefinition.createBuilder(new TransInstance(instance, _getterClass, (ARG) args[0]));
            }
            
        }
        
        protected class InstanceImpl implements Instance {
            protected final Object        _proxy     ;
            protected final TransInstance _transition;
            
            public InstanceImpl(TransInstance transition) {
                super();
                _transition = transition;
                _proxy = Proxy.newProxyInstance(_builderClass.getClassLoader(), new Class<?>[] {_builderClass}, this);
            }
            
            @SuppressWarnings("unchecked")
            @Override public <T extends GetBase> T get(Class<T> key) {
                if(key.isInstance(_proxy)) {
                    return (T) _proxy;
                } else {
                    return null;
                }
            }
            
            @Override public Object getValue(final Class<?> key) {
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
    
            @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return _methodById.get(new MethodIdentifier(method)).invoke(this, args);
            }

            @SuppressWarnings("unchecked")
            public BUILDER proxy() { return (BUILDER) _proxy; }
            
        }
    
        public Definition(Class<BUILDER> builderClass) {
            _builderClass = builderClass;
            _methodById   = new HashMap<MethodIdentifier, MethodInvoker<?>>();
        }
        
        public void init() {
            Set<Class<?>> interfaces = getInterfaces(_builderClass);
            for(final Class<?> intf : interfaces) {
                if(!Base.class.isAssignableFrom(intf)) {
                    throw new IllegalArgumentException("Error: interface is not builder base interface: " + intf.getName());
                }
                for(Method method : MegaBuilder.getMethodsDeclaredAfter(intf, Base.class)) {
                    final MethodIdentifier id = new MethodIdentifier(method);
                    if(!_methodById.containsKey(id)) {
                        final MethodInvoker<?> invoker;
                        {}   if(GetBase    .class.isAssignableFrom(intf))
                            invoker = newMethodInvokerGet       (getIntfDeclaredAfter((Class<GetBase  >) intf, GetBase  .class));
                        else if(TransBase  .class.isAssignableFrom(intf))
                            invoker = newMethodInvokerTransition(getIntfDeclaredAfter((Class<TransBase>) intf, TransBase.class));
                        else if(BuiltValues.class.isAssignableFrom(intf))
                            invoker = MethodInvokerBuiltValues.INSTANCE;
                        else if(BuiltBase  .class.isAssignableFrom(intf))
                            invoker = newMethodInvokerBuilt     (getIntfDeclaredAfter((Class<BuiltBase>) intf, BuiltBase.class));
                        else throw new IllegalArgumentException("Error: interface is not builder get/trans/built interface: " + intf.getName());
                        _methodById.put(id, invoker);
                    }
                }
            }
            try {
                _methodById.put(new MethodIdentifier(Object.class.getMethod("equals"  , Object.class)), MethodInvokerEquals  .INSTANCE);
                _methodById.put(new MethodIdentifier(Object.class.getMethod("hashCode"              )), MethodInvokerHashCode.INSTANCE);
            } catch(NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        
        public MethodInvokerGet<Object> newMethodInvokerGet(Class<? extends GetBase> intf) {
            if(1 != intf.getMethods().length) throw new IllegalArgumentException("Illegal get-base extension. No single get method.");
            Method method = intf.getMethods()[0];
            if(false
                || (Void.TYPE == method.getReturnType())
                || (0 != method.getParameterTypes().length)
            ) {
                throw new IllegalArgumentException("Illegal get-base extension. Invalid get method.");
            }
            return new MethodInvokerGet<Object>(intf);
        }
        
        /**
         * There must be diff with parent and dest and find delta getter.
         * Delta getter also must be equal name and equal type with transition.
         * 
         * @param intf
         * @return
         */
        public <RESULT extends T, T extends TransBase> MethodInvokerTransition<RESULT, Object> newMethodInvokerTransition(
            /*Class<RESULT> parent,*/ Class<T> intf
        ) {
            @SuppressWarnings("unchecked")
            Class<RESULT> parent = (Class<RESULT>) _builderClass;
            
            if(1 != intf.getTypeParameters().length)
                throw new IllegalArgumentException("Illegal trans-base extension. No type parameter.");
            
            if(1 != intf.getTypeParameters()[0].getBounds().length)
                throw new IllegalArgumentException("Illegal trans-base extension. No type parameter.");
            
            Class<?> actual = (Class<?>) intf.getTypeParameters()[0].getBounds()[0];
            
            if(!GetBase.class.isAssignableFrom(actual))
                throw new IllegalArgumentException("Illegal trans-base extension. Type parameter must be inherited from GetBase.");
            
            @SuppressWarnings("unchecked")
            Class<GetBase> baseGetterClass = (Class<GetBase>) actual;
            Class<? extends GetBase> actualGetterClass = MegaBuilder.getIntfDeclaredAfter(baseGetterClass, GetBase.class);
            
            if(1 != intf.getMethods().length)
                throw new IllegalArgumentException("Illegal trans-base extension. No single transition method.");
            
            Method method = intf.getMethods()[0];
            if(false
                || !Base.class.isAssignableFrom(method.getReturnType())
                || (1 != method.getParameterTypes().length)
            ) {
                throw new IllegalArgumentException("Illegal trans-base extension. Invalid transition method.");
            }
            Class<RESULT> estimatedBuilder = MegaBuilder.getIntfDeclaredAfter(parent, intf);
            final Class<RESULT> actualBuilder;
            Type[] actualBuilderGenericInterfaceTypes = estimatedBuilder.getGenericInterfaces();
            outer: do {
                for(Type actualBuilderGenericInterfaceType : actualBuilderGenericInterfaceTypes) {
                    if(actualBuilderGenericInterfaceType instanceof ParameterizedType) {
                        ParameterizedType actualBuilderGenericInterfaceParameterizedType = (ParameterizedType) actualBuilderGenericInterfaceType;
                        Class<?> actualBuilderGenericInterfaceParameterizedTypeClass = (Class<?>) actualBuilderGenericInterfaceParameterizedType.getRawType();
                        if(intf.isAssignableFrom(actualBuilderGenericInterfaceParameterizedTypeClass)) {
                            Type[] actualBuilderGenericInterfaceParameterizedTypeArguments = actualBuilderGenericInterfaceParameterizedType.getActualTypeArguments();
                            actualBuilder = (Class<RESULT>) actualBuilderGenericInterfaceParameterizedTypeArguments[0];
                            break outer;
                        }
                    }
                }
                actualBuilder = null;
            } while(false);
            return new MethodInvokerTransition<RESULT, Object>(getDefinition(actualBuilder), actualGetterClass);
        }
    
        public <RESULT extends BuiltBase> MethodInvokerBuilt newMethodInvokerBuilt(Class<RESULT> intf) {
            if(1 != intf.getMethods().length) throw new IllegalArgumentException("Illegal trans-base extension. No single transition method.");
            Method method = intf.getMethods()[0];
            if(false
                || !_resultClass.isAssignableFrom(method.getReturnType())
                || (0 != method.getParameterTypes().length)
            ) {
                throw new IllegalArgumentException("Illegal built-base extension. Invalid build method.");
            }
            return MethodInvokerBuilt_INSTANCE;
        }

        public InstanceImpl createInstance(TransInstance transition) {
            return new InstanceImpl(transition);
        }
        
        public BUILDER createBuilder(TransInstance transition) {
            /*
            final InstanceImpl instance = createInstance(transition);
            Object tempResult = Proxy.newProxyInstance(_builderClass.getClassLoader(), new Class<?>[] {_builderClass}, instance);
            instance.setProxy(tempResult);
            @SuppressWarnings("unchecked")
            BUILDER result = (BUILDER) tempResult;
            return result;
            */
            return createInstance(transition).proxy();
        }
        
    }

    public INITIAL_BUILDER createBuilder() {
        return _initialBuilderDefinition.createBuilder(null); 
    }

}