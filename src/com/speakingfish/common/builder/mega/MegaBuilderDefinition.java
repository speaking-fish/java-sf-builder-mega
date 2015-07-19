package com.speakingfish.common.builder.mega;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.speakingfish.common.builder.mega.MegaBuilder.*;

import static com.speakingfish.common.builder.mega.MegaBuilderHelper.*;

public class MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER extends Base> {
    
    protected final Map<Class<? extends Base>, Definition<? extends Base>> _definitionByClass
    =       new HashMap<Class<? extends Base>, Definition<? extends Base>>();

    protected final ClassBuilder<RESULT_CLASS   >  _resultClassBuilder      ;
    protected final Definition  <INITIAL_BUILDER>  _initialBuilderDefinition;
    
    protected final InstanceMethodInvoker<RESULT_CLASS> InstanceMethodInvoker_BuiltBase_build = new InstanceMethodInvoker<RESULT_CLASS>() {
        @Override public RESULT_CLASS invoke(Instance instance, Object[] args) {
            return _resultClassBuilder.build((BuiltValues) instance.proxy());
        }
    };

    public MegaBuilderDefinition(
        Class       <INITIAL_BUILDER> initialBuilderClass,
        ClassBuilder<RESULT_CLASS   > resultClassBuilder
    ) {
        _resultClassBuilder       = resultClassBuilder;
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
        for(final Class<?> intf : src.getInterfaces()) {
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
        protected final Map<MethodId, InstanceMethodInvoker<?>> _methodById;
        
        /**
         * 
         * RESULT get[NAME]()
         *
         * @param <RESULT>
         */
        protected class MethodInvokerGet<RESULT> implements InstanceMethodInvoker<RESULT> {
            
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
        protected class MethodInvokerTransition<RESULT extends Base, ARG> implements InstanceMethodInvoker<RESULT> {
            
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
                _proxy = Proxy.newProxyInstance(_builderClass.getClassLoader(), new Class<?>[] {_builderClass, BuiltValues.class}, this);
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
                return _methodById.get(new MethodId(method)).invoke(this, args);
            }

            @SuppressWarnings("unchecked")
            @Override public BUILDER proxy() { return (BUILDER) _proxy; }
            
        }
    
        public Definition(Class<BUILDER> builderClass) {
            _builderClass = builderClass;
            _methodById   = new HashMap<MethodId, InstanceMethodInvoker<?>>();
        }
        
        @SuppressWarnings("unchecked")
        public void init() {
            _methodById.put(MethodId_BuiltValues_get, InstanceMethodInvoker_BuiltValues_get);
            
            _methodById.put(MethodId_Object_equals  , InstanceMethodInvoker_Object_equals  );
            _methodById.put(MethodId_Object_hashCode, InstanceMethodInvoker_Object_hashCode);
            _methodById.put(MethodId_Object_toString, InstanceMethodInvoker_Object_toString);
            
            final Set<Class<?>> interfaces = getInterfaces(_builderClass);
            for(final Class<?> intf : interfaces) {
                if(!Base.class.isAssignableFrom(intf)) {
                    throw new IllegalArgumentException("Error: interface is not builder base interface: " + intf.getName());
                }
                for(final Method method : MegaBuilderHelper.getMethodsDeclaredAfter(intf, Base.class)) {
                    final MethodId id = new MethodId(method);
                    if(!_methodById.containsKey(id)) {
                        final InstanceMethodInvoker<?> invoker;
                        {}   if(GetBase    .class.isAssignableFrom(intf)) invoker = instanceMethodInvoker_GetBase_method  (getInterfaceDeclaredAfter((Class<GetBase  >) intf, GetBase  .class));
                        else if(TransBase  .class.isAssignableFrom(intf)) invoker = instanceMethodInvoker_TransBase_method(getInterfaceDeclaredAfter((Class<TransBase>) intf, TransBase.class));
                        else if(BuiltBase  .class.isAssignableFrom(intf)) invoker = InstanceMethodInvoker_BuiltBase_build; 
                        else throw new IllegalArgumentException("Error: interface is not builder get/trans/built interface: " + intf.getName());
                        _methodById.put(id, invoker);
                    }
                }
            }
        }
        
        public MethodInvokerGet<Object> instanceMethodInvoker_GetBase_method(Class<? extends GetBase> intf) {
            if(1 != intf.getMethods().length) throw new IllegalArgumentException("Illegal get-base extension. No single get method.");
            final Method method = intf.getMethods()[0];
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
        public <RESULT extends T, T extends TransBase> MethodInvokerTransition<RESULT, Object> instanceMethodInvoker_TransBase_method(Class<T> intf) {
            @SuppressWarnings("unchecked")
            final Class<RESULT> parent = (Class<RESULT>) _builderClass;
            
            if(1 != intf.getTypeParameters().length)
                throw new IllegalArgumentException("Illegal trans-base extension. No type parameter.");
            
            if(1 != intf.getTypeParameters()[0].getBounds().length)
                throw new IllegalArgumentException("Illegal trans-base extension. No type parameter.");
            
            final Class<?> actual = (Class<?>) intf.getTypeParameters()[0].getBounds()[0];
            
            if(!GetBase.class.isAssignableFrom(actual))
                throw new IllegalArgumentException("Illegal trans-base extension. Type parameter must be inherited from GetBase.");
            
            @SuppressWarnings("unchecked")
            final Class<GetBase> baseGetterClass = (Class<GetBase>) actual;
            final Class<? extends GetBase> actualGetterClass = MegaBuilderHelper.getInterfaceDeclaredAfter(baseGetterClass, GetBase.class);
            
            if(1 != intf.getMethods().length)
                throw new IllegalArgumentException("Illegal trans-base extension. No single transition method.");
            
            final Method method = intf.getMethods()[0];
            if(false
                || !Base.class.isAssignableFrom(method.getReturnType())
                || (1 != method.getParameterTypes().length)
            ) {
                throw new IllegalArgumentException("Illegal trans-base extension. Invalid transition method.");
            }
            final Class<RESULT> transition = MegaBuilderHelper.getInterfaceDeclaredAfter(parent, intf);
            final Class<RESULT> actualBuilder;
            outer: do {
                for(final Type type : transition.getGenericInterfaces()) {
                    if(type instanceof ParameterizedType) {
                        final ParameterizedType parameterizedType = (ParameterizedType) type;
                        final Class<?> checkIntf = (Class<?>) parameterizedType.getRawType();
                        if(intf.isAssignableFrom(checkIntf)) {
                            @SuppressWarnings("unchecked")
                            final Class<RESULT> tempActualBuilder = (Class<RESULT>) parameterizedType.getActualTypeArguments()[0];
                            actualBuilder = tempActualBuilder; 
                            break outer;
                        }
                    }
                }
                actualBuilder = null;
            } while(false);
            return new MethodInvokerTransition<RESULT, Object>(getDefinition(actualBuilder), actualGetterClass);
        }
    
        public InstanceImpl createInstance(TransInstance transition) {
            return new InstanceImpl(transition);
        }
        
        public BUILDER createBuilder(TransInstance transition) {
            return createInstance(transition).proxy();
        }
        
    }

    public INITIAL_BUILDER createBuilder() {
        return _initialBuilderDefinition.createBuilder(null); 
    }

}