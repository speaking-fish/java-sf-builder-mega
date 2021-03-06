package com.speakingfish.common.builder.mega;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.speakingfish.common.builder.mega.impl.*;

/**
 * 
 * MegaBuilder facade
 * 
 * Single class per (RESULT_CLASS + INITIAL_BUILDER + ClassBuilder instance
 *
 * @param <CONTEXT> local context parameter
 * @param <RESULT_CLASS> result class
 * @param <INITIAL_BUILDER> initial builder interface
 */
public class MegaBuilder<CONTEXT, RESULT_CLASS, INITIAL_BUILDER extends MegaBuilder.Base> {
    
    /** Marker interface */
    public interface Base {}
    
    /** Getter base marker interface */
    public interface GetBase extends Base {}
    
    /** transition base marker interface */
    public interface TransBase extends Base {}

    /**
     * Built base interface
     *
     * @param <RESULT_CLASS> result class
     */
    public interface BuiltBase<RESULT_CLASS> extends Base {
        RESULT_CLASS build();
    }

    /**
    *
    * Result class creation is here
    *
    * @param <RESULT_CLASS> result class
    */
   public interface ClassBuilder<CONTEXT, RESULT_CLASS> {
       RESULT_CLASS build(CONTEXT context, BuiltValues values);
   }
    
    /**
     * Built values holder
     *  
     * @see ClassBuilder#build(BuiltValues)
     */
    public interface BuiltValues extends Base {
        
        /**
         * @param key getter class
         * @return getter interface or null if no value assigned for this getter or key is wrong getter type
         */
        <T extends GetBase> T get(Class<T> key);
        
        /**<p>
         * @param key transition class ("super" used for compatibility with parameterized classes to to avoid compilation error)
         * @return transition interface of corresponding getter interface for set default value,
         *         that returns corresponding getter interface with that default value if no value assigned for that getter type
         *         or getter interface
         * </p>
         * <p>        
         * Example:
         * <code>
         * values.getDefault(Trans_first.class).first(-1).first()
         * </code>
         * </p>
         * <p>
         * For parameterized classes this method is a little bit more complex, because you must specify type:
         * <code>
         * values.&lt;Trans_third&lt;TYPE or ? for any, ?&gt;&gt;getDefault(Trans_third.class).third(null).third()
         * </code>
         * </p>
         */
        <T extends TransBase> T getDefault(Class<? super T> key);
        
        /**
         * @param key getter class
         * @return value <b>Untyped warning! You must correct cast result Object to actual type - can't check this in compile time!</b>
         * If no value available, not null returns, but exception was thrown.
         */
        Object getValue(final Class<? extends GetBase> key);

        /**
         * @param key
         * @return true - has value
         */
        boolean hasValue(final Class<? extends GetBase> key);
        
        /**
         *  
         * @param key getter class
         * @param defaultValue <b>Untyped warning! You must correct pass defaultValue of actual type - can't check this in compile time!</b>
         * @return getter interface with actual value, or if no value assigned, defaultValue wrapped with getter interface or null if key is wrong getter type
         */
        <T extends GetBase> T get(Class<T> key, Object defaultValue);
    }
    
    public class Instance {
        
        protected final CONTEXT _context;
        
        public Instance(CONTEXT context) { _context = context; }
        
        public CONTEXT context() { return _context; }
        
        public INITIAL_BUILDER create() { return definition().create(this); }
        
        public RESULT_CLASS build(BuilderInstance<CONTEXT, ? extends Base, RESULT_CLASS, INITIAL_BUILDER> instance) {
            return MegaBuilder.this.build(instance, this.context());
        }
    }

    /**
     * Create new definition class.
     * Useful for create MegaBuilder instantiation variations
     * @param initialBuilderClass
     * @return
     */
    public static <RESULT_CLASS, INITIAL_BUILDER extends Base
    > MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> newDefinition(
        Class<INITIAL_BUILDER> initialBuilderClass
    ) {
        return new MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>(initialBuilderClass);
    }

    /**
     * Create MegaBuilder.
     * Useful for create MegaBuilder instantiation variations.
     * @param builderDefinition
     * @param classBuilder
     * @return
     */
    public static <CONTEXT, RESULT_CLASS, INITIAL_BUILDER extends Base
    > MegaBuilder<CONTEXT, RESULT_CLASS, INITIAL_BUILDER> newMegaBuilder(
        MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> builderDefinition,
        ClassBuilder         <CONTEXT, RESULT_CLASS        > classBuilder
    ) {
        return new MegaBuilder<CONTEXT, RESULT_CLASS, INITIAL_BUILDER>(
            builderDefinition,
            classBuilder
            );
    }
    
    /**
     * Create MegaBuilder.
     * Useful in some cases.
     * @param initialBuilderClass
     * @param classBuilder
     * @return
     */
    public static <CONTEXT, RESULT_CLASS, INITIAL_BUILDER extends Base
    > MegaBuilder<CONTEXT, RESULT_CLASS, INITIAL_BUILDER> newMegaBuilder(
        Class       <INITIAL_BUILDER      > initialBuilderClass,
        ClassBuilder<CONTEXT, RESULT_CLASS> classBuilder
    ) {
        return newMegaBuilder(
            MegaBuilder.<RESULT_CLASS, INITIAL_BUILDER>newDefinition(initialBuilderClass),
            classBuilder
            );
    }

    /**
     * Create initial builder.
     * Five stars method.
     * @param initialBuilderClass
     * @param classBuilder
     * @return
     */
    public static <CONTEXT, RESULT_CLASS, INITIAL_BUILDER extends Base
    > INITIAL_BUILDER newBuilder(
        Class       <INITIAL_BUILDER      > initialBuilderClass,
        CONTEXT                             context            ,
        ClassBuilder<CONTEXT, RESULT_CLASS> classBuilder
    ) {
        return newMegaBuilder(initialBuilderClass, classBuilder).create(context);
    }

    /**
     * Create initial builder.
     * Useful for create MegaBuilder instantiation variations.
     * @param initialBuilderClass
     * @param classBuilder
     * @return
     */
    public static <CONTEXT, RESULT_CLASS, INITIAL_BUILDER extends Base
    > INITIAL_BUILDER newBuilder(
        MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> builderDefinition,
        CONTEXT                                              context          ,
        ClassBuilder         <CONTEXT, RESULT_CLASS        > classBuilder
    ) {
        return newMegaBuilder(builderDefinition, classBuilder).create(context);
    }
    
    protected final MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> _definition  ;
    protected final ClassBuilder         <CONTEXT, RESULT_CLASS        > _classBuilder;
    
    protected Map<Class<? extends Base>, Method> _creatorMethodByBuiltClass = null;
    
    public MegaBuilder(
        MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> definition  ,
        ClassBuilder         <CONTEXT, RESULT_CLASS        > classBuilder
    ) {
        super();
        _definition   = definition  ;
        _classBuilder = classBuilder;
        for(Method method : classBuilder.getClass().getMethods()) {
            if(true
                && "build".equals(method.getName())
                && method.getReturnType().equals(definition.resultClass())
            ) {
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if(true
                    && (2 == parameterTypes.length)
                    && (Base.class.isAssignableFrom(parameterTypes[1]))
                ) {
                    final MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>.Definition<CONTEXT, ? extends Base>
                    bulderDefinition = definition.<CONTEXT>definitionByClassMap().get(parameterTypes[1]);
                    if(null != bulderDefinition) {
                        if(null == _creatorMethodByBuiltClass) {
                            _creatorMethodByBuiltClass = new HashMap<Class<? extends Base>, Method>(); 
                        }
                        method.setAccessible(true);
                        _creatorMethodByBuiltClass.put(bulderDefinition.builderClass(), method);
                    }
                }
            }
        }
    }
    
    public RESULT_CLASS build(
        BuilderInstance<CONTEXT, ? extends Base, RESULT_CLASS, INITIAL_BUILDER> instance,
        CONTEXT context
    ) {
        if(null != _creatorMethodByBuiltClass) {
            final Method creator = _creatorMethodByBuiltClass.get(instance.definition().builderClass());
            if(null != creator) {
                try {
                    @SuppressWarnings("unchecked")
                    final RESULT_CLASS result = (RESULT_CLASS) creator.invoke(_classBuilder, context, instance.proxy());
                    return result;
                } catch(ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return _classBuilder.build(context, (BuiltValues) instance.proxy());
    }
    
    public INITIAL_BUILDER create(CONTEXT context) { return definition().create(new Instance(context)); }

    public MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> definition() { return _definition; }
    
}
