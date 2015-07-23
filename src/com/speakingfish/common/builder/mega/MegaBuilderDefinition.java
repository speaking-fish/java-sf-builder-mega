package com.speakingfish.common.builder.mega;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.speakingfish.common.builder.mega.MegaBuilder.*;
import com.speakingfish.common.builder.mega.impl.*;

import static com.speakingfish.common.builder.mega.impl.MegaBuilderHelper.*;

/**
 * 
 * Single class per INITIAL_BUILDER
 *
 * @param <RESULT_CLASS>
 * @param <INITIAL_BUILDER>
 */
public class MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER extends Base> {
    
    protected static final MethodInvoker<Object, BuilderInstance<Object, Base, Object, Base>
    > MethodInvoker_BuiltBase_build = new MethodInvoker<Object, BuilderInstance<Object, Base, Object, Base>>() {
        @Override public Object invoke(BuilderInstance<Object, Base, Object, Base> instance, Object[] args) {
            return instance.builder().build(instance);
        }
    };
    
    
    protected final Map<Class<? extends Base>, Definition<?, ? extends Base>> _definitionByClass
    =       new HashMap<Class<? extends Base>, Definition<?, ? extends Base>>();

    protected final Map<Class<? extends GetBase>, GetterValueDefinition<? extends Base, ?>> _getterValueDefinitionByClass
    =       new HashMap<Class<? extends GetBase>, GetterValueDefinition<? extends Base, ?>>();
    
    protected final Definition<?, INITIAL_BUILDER> _initialDefinition;
    
    protected Class<RESULT_CLASS> _resultClass = null;
    
    public MegaBuilderDefinition(Class <INITIAL_BUILDER> initialBuilderClass) {
        _initialDefinition = getDefinition(initialBuilderClass);
    }
    
    @SuppressWarnings("unchecked")
    public <CONTEXT> MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>.Definition<CONTEXT, INITIAL_BUILDER> initialDefinition() {
        return (MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>.Definition<CONTEXT, INITIAL_BUILDER>) _initialDefinition;
    }
    
    public Class<RESULT_CLASS> resultClass() { return _resultClass; }
    
    protected <CONTEXT, BUILDER extends Base> Definition<CONTEXT, BUILDER> getDefinition(Class<BUILDER> builderClass) {
        @SuppressWarnings("unchecked")
        Definition<CONTEXT, BUILDER> result = (Definition<CONTEXT, BUILDER>) _definitionByClass.get(builderClass);
        if(null == result) {
            result = new Definition<CONTEXT, BUILDER>(builderClass);
            _definitionByClass.put(builderClass, result);
            // put reference to initialized class before initialize to allow resolving references to this class in initialization
            result.init();
        }
        return result;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <CONTEXT> Map<Class<? extends Base>, Definition<CONTEXT, ? extends Base>> definitionByClassMap() {
        return (Map<Class<? extends Base>, Definition<CONTEXT, ? extends Base>>) (Map) _definitionByClass;
    }

    public <CONTEXT> INITIAL_BUILDER create(
        MegaBuilder<CONTEXT, RESULT_CLASS, INITIAL_BUILDER>.Instance builderInstance
    ) {
        return this.<CONTEXT>initialDefinition().create(
            builderInstance, 
            (TransInstance<CONTEXT, ? extends Base, RESULT_CLASS, INITIAL_BUILDER>) null
            );
    }
    
    public class Definition<CONTEXT, BUILDER extends Base>
    extends CommonInstanceDefinition<BuilderInstance<CONTEXT, BUILDER, RESULT_CLASS, INITIAL_BUILDER>> {
        
        protected final Class<?>[]_instanceInterfaces;
        
        protected final Class<BUILDER> _builderClass;
        protected Boolean _isBuilt = null;
        
        public Definition(Class<BUILDER> builderClass) {
            _builderClass = builderClass;
            _instanceInterfaces = new Class<?>[] {_builderClass, BuiltValues.class};
        }
        
        public Class<BUILDER> builderClass() { return _builderClass; }
        public Boolean        isBuilt     () { return _isBuilt     ; }
        
        @Override public Class<?>[]     instanceInterfaces () { return _instanceInterfaces; }
        @Override public ClassLoader    instanceClassLoader() { return builderClass().getClassLoader(); }
        @Override public CommonInstanceDefinition<?> parent() { return InstanceDefinition_BuiltValues; }

        @SuppressWarnings("unchecked")
        public void init() {
            final Set<Class<?>> interfaces = getInterfaces(_builderClass);
            for(final Class<?> intf : interfaces) {
                if(!Base.class.isAssignableFrom(intf)) {
                    throw new IllegalArgumentException("Error: interface is not builder base interface: " + intf.getName());
                }
                for(final Method method : MegaBuilderHelper.getMethodsDeclaredAfter(intf, Base.class)) {
                    final MethodId id = new MethodId(method);
                    if(!_methodById.containsKey(id)) {
                        final MethodInvoker<?, ? extends BuilderInstance<?, BUILDER, RESULT_CLASS, INITIAL_BUILDER>> invoker;
                        {}     if(GetBase    .class.isAssignableFrom(intf)) {
                            {} if(GetBase    .class.equals          (intf)) continue;
                            invoker = methodInvoker_GetBase_method  (getInterfaceDeclaredAfter((Class<GetBase     >) intf, GetBase  .class));
                        } else if(TransBase  .class.isAssignableFrom(intf)) {
                            {} if(TransBase  .class.equals          (intf)) continue;
                            invoker = methodInvoker_TransBase_method(getInterfaceDeclaredAfter((Class<TransBase   >) intf, TransBase.class));
                        } else if(BuiltBase  .class.isAssignableFrom(intf)) {
                            {} if(BuiltBase  .class.equals          (intf)) continue;
                            invoker = methodInvoker_BuiltBase_method(getInterfaceDeclaredAfter((Class<BuiltBase<?>>) intf, BuiltBase.class));
                        } else {
                            throw new IllegalArgumentException("Error: interface is not builder get/trans/built interface: " + intf.getName());
                        }
                        
                        _methodById.put(id, invoker);
                    }
                }
            }
            if(null == _isBuilt) {
                _isBuilt = false;
            }
        }
        
        public <T extends BuiltBase<?>> MethodInvoker<?, BuilderInstance<?, BUILDER, RESULT_CLASS, INITIAL_BUILDER>
        > methodInvoker_BuiltBase_method(Class<T> intf) {
            final Method[] methods = intf.getMethods();
            if(1 != methods.length) {
                throw new IllegalArgumentException("Illegal built-base extension. No single get method in interface: " + intf.getName());
            }
            
            Type[] genericInterfaces = intf.getGenericInterfaces();
            
            if(1 != genericInterfaces.length) {
                throw new IllegalArgumentException("Illegal built-base extension. More than one super interface: " + intf.getName());
            }
            
            if(!(genericInterfaces[0] instanceof ParameterizedType)) {
                throw new IllegalArgumentException("Illegal built-base extension. No type parameter in super interface: " + intf.getName());
            }
            
            final ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];
            
            final Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if(1 != typeArguments.length) {
                throw new IllegalArgumentException("Illegal built-base extension. No type parameter in super interface: " + intf.getName());
            }
            
            final Class<RESULT_CLASS> actual;
            if(typeArguments[0] instanceof Class) {
                @SuppressWarnings("unchecked")
                final Class<RESULT_CLASS> tempActual = (Class<RESULT_CLASS>) typeArguments[0];
                actual = tempActual;
            } else {
                @SuppressWarnings("unchecked")
                final Class<RESULT_CLASS> tempActual = (Class<RESULT_CLASS>) ((ParameterizedType) typeArguments[0]).getRawType();
                actual = tempActual;
            }
            
            if(null == _resultClass) {
                _resultClass = actual;
            } else if(!_resultClass.equals(actual)) {
                throw new IllegalArgumentException("Illegal built-base extension: Multiply result classes detected: " + _resultClass.getName() + ", " + actual.getName());
            }
            
            _isBuilt = true;
            
            @SuppressWarnings({"unchecked", "rawtypes"})
            final MethodInvoker<?, BuilderInstance<?, BUILDER, RESULT_CLASS, INITIAL_BUILDER>> result
            = (MethodInvoker<?, BuilderInstance<?, BUILDER, RESULT_CLASS, INITIAL_BUILDER>>) (MethodInvoker) MethodInvoker_BuiltBase_build;
            
            return result;
        }
        
        public <GETTER extends GetBase> MethodInvokerGet<Object> methodInvoker_GetBase_method(final Class<GETTER> intf) {
            final Method[] methods = intf.getMethods();
            if(1 != methods.length) {
                throw new IllegalArgumentException("Illegal get-base extension. No single get method in interface: " + intf.getName());
            }
            
            final Method method = methods[0];
            if(false
                || (Void.TYPE == method.getReturnType())
                || (0 != method.getParameterTypes().length)
            ) {
                throw new IllegalArgumentException("Illegal get-base extension. Invalid get method: " + method.toString());
            }
            
            GetterValueDefinition<? extends Base, ?> getterValueDefinition = _getterValueDefinitionByClass.get(intf);
            if(null == getterValueDefinition) {
                final Class<?>[] interfaces = new Class<?>[] {intf};
                getterValueDefinition = new GetterValueDefinition<GETTER, Object>(method) {
                    @Override public ClassLoader instanceClassLoader() { return intf.getClassLoader(); }
                    @Override public Class<?>[]  instanceInterfaces () { return interfaces           ; }
                };
                _getterValueDefinitionByClass.put(intf, getterValueDefinition);
            }
            
            return new MethodInvokerGet<Object>(intf);
        }
        
        public <RESULT extends T, T extends TransBase> MethodInvokerTransition<RESULT, Object> methodInvoker_TransBase_method(Class<T> intf) {
            @SuppressWarnings("unchecked")
            final Class<RESULT> parent = (Class<RESULT>) _builderClass;
            
            final TypeVariable<Class<T>>[] typeParameters = intf.getTypeParameters();

            final Class<GetBase> baseGetterClass;
            
            outer: do {
                for(TypeVariable<Class<T>> typeVariable : typeParameters) {
                    final Type[] bounds = typeVariable.getBounds(); 
                    
                    for(Type bound : bounds) {
                        final Class<?> actual;
                        if(bound instanceof Class<?>) {
                            final Class<?> tempActual = (Class<?>) bound;
                            actual = tempActual;
                        } else {
                            final Class<?> tempActual = (Class<?>) ((ParameterizedType) bound).getRawType();
                            actual = tempActual;
                        }
                        
                        if(GetBase.class.isAssignableFrom(actual)) {
                            @SuppressWarnings("unchecked")
                            final Class<GetBase> tempBaseGetterClass = (Class<GetBase>) actual;
                            baseGetterClass = tempBaseGetterClass;
                            break outer;
                        }
                        
                    }
                }
                
                throw new IllegalArgumentException("Illegal trans-base extension. Type parameter must be inherited from GetBase. Interface: " + intf.getName());
            } while(false);
            
            
            final Class<? extends GetBase> actualGetterClass = MegaBuilderHelper.getInterfaceDeclaredAfter(baseGetterClass, GetBase.class);
            
            final Method[] methods = intf.getMethods();
            if(1 != methods.length)
                throw new IllegalArgumentException("Illegal trans-base extension. No single transition method in interface: " + intf.getName());
            
            final Method method = methods[0];
            if(false
                || !Base.class.isAssignableFrom(method.getReturnType())
                || (1 != method.getParameterTypes().length)
            ) {
                throw new IllegalArgumentException("Illegal trans-base extension. Invalid transition method: " + method.toString());
            }
            final Class<RESULT> transition = MegaBuilderHelper.getInterfaceDeclaredAfter(parent, intf);
            final Class<RESULT> actualBuilder;
            outer: do {
                for(final Type type : transition.getGenericInterfaces()) {
                    if(type instanceof ParameterizedType) {
                        final ParameterizedType parameterizedType = (ParameterizedType) type;
                        final Class<?> checkIntf = (Class<?>) parameterizedType.getRawType();
                        if(intf.isAssignableFrom(checkIntf)) {
                            Type[] typeArguments = parameterizedType.getActualTypeArguments();
                            for(Type typeArgument : typeArguments) {
                                if(typeArgument instanceof Class<?>) {
                                    @SuppressWarnings("unchecked")
                                    final Class<RESULT> tempActualBuilder = (Class<RESULT>) typeArgument;
                                    actualBuilder = tempActualBuilder; 
                                    break outer;
                                } else if(typeArgument instanceof TypeVariable) {
                                    // skip it
                                } else {
                                    @SuppressWarnings("unchecked")
                                    final Class<RESULT> tempActualBuilder = (Class<RESULT>) ((ParameterizedType) typeArgument).getRawType();
                                    actualBuilder = tempActualBuilder; 
                                    break outer;
                                }
                            }
                        }
                    }
                }
                actualBuilder = null;
            } while(false);
            return new MethodInvokerTransition<RESULT, Object>(MegaBuilderDefinition.this.<CONTEXT, RESULT>getDefinition(actualBuilder), actualGetterClass);
        }
    
        public BuilderInstance<CONTEXT, BUILDER, RESULT_CLASS, INITIAL_BUILDER> createInstance(
            MegaBuilder  <CONTEXT,                 RESULT_CLASS, INITIAL_BUILDER>.Instance builder   ,
            TransInstance<CONTEXT, ? extends Base, RESULT_CLASS, INITIAL_BUILDER>          transition
        ) {
            return new BuilderInstance<CONTEXT, BUILDER, RESULT_CLASS, INITIAL_BUILDER>(this, builder, transition);
        }
        
        public BUILDER create(
            MegaBuilder  <CONTEXT                , RESULT_CLASS, INITIAL_BUILDER>.Instance builderInstance,
            TransInstance<CONTEXT, ? extends Base, RESULT_CLASS, INITIAL_BUILDER>          transition
        ) {
            return createInstance(builderInstance, transition).proxy();
        }
        
        public <GETTER extends GetBase, VALUE_TYPE> GETTER createGetterValue(Class<GETTER> key, VALUE_TYPE defaultValue) {
            @SuppressWarnings("unchecked")
            final GetterValueDefinition<GETTER, VALUE_TYPE> getterValueDefinition
            =    (GetterValueDefinition<GETTER, VALUE_TYPE>) _getterValueDefinitionByClass.get(key);
            
            if(null == getterValueDefinition) {
                return null;
            } else {
                return getterValueDefinition.create(defaultValue);
            }
        }
        
        /**
         * 
         * RESULT get[NAME]()
         *
         * @param <RESULT>
         */
        protected class MethodInvokerGet<RESULT>
        implements MethodInvoker<RESULT, BuilderInstance<?, BUILDER, RESULT_CLASS, INITIAL_BUILDER>> {
            
            protected final Class<? extends GetBase> _getIntf;
    
            public MethodInvokerGet(Class<? extends GetBase> getIntf) {
                _getIntf = getIntf;
            }
    
            @SuppressWarnings("unchecked")
            @Override public RESULT invoke(BuilderInstance<?, BUILDER, RESULT_CLASS, INITIAL_BUILDER> instance, Object[] args) {
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
        protected class MethodInvokerTransition<RESULT extends Base, ARG>
        implements MethodInvoker<RESULT, BuilderInstance<CONTEXT, BUILDER, RESULT_CLASS, INITIAL_BUILDER>> {
            
            protected final Definition<CONTEXT, RESULT  > _resultDefinition;
            protected final Class     <? extends GetBase> _getterClass     ;
    
            public MethodInvokerTransition(
                Definition<CONTEXT, RESULT  > resultDefinition,
                Class     <? extends GetBase> getterClass
            ) {
                _resultDefinition = resultDefinition;
                _getterClass      = getterClass     ;
            }
    
            @SuppressWarnings("unchecked")
            @Override public RESULT invoke(BuilderInstance<CONTEXT, BUILDER, RESULT_CLASS, INITIAL_BUILDER> instance, Object[] args) {
                return _resultDefinition.create(
                    instance.builder(),
                    new TransInstance<CONTEXT, BUILDER, RESULT_CLASS, INITIAL_BUILDER>(instance, _getterClass, (ARG) args[0])
                    );
            }
            
        }
        
    }
    
}