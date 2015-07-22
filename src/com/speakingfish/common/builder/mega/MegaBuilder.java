package com.speakingfish.common.builder.mega;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.speakingfish.common.builder.mega.impl.*;

/**
 * 
 * MegaBuilder facade
 *
 * @param <RESULT_CLASS> result class
 * @param <INITIAL_BUILDER> initial builder interface
 */
public class MegaBuilder<RESULT_CLASS, INITIAL_BUILDER extends MegaBuilder.Base> {
    
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
    public interface ClassBuilder<RESULT_CLASS> {
        RESULT_CLASS build(BuiltValues values);
    }
   
    /**
     * Field values holder
     *  
     * @see ClassBuilder#build(BuiltValues)
     */
    public interface BuiltValues extends Base {
        /**
         * @param key getter class
         * @return getter interface or null if no value assigned for this getter or key is wrong getter type
         */
        <T extends GetBase> T get(Class<T> key);
        /**
         * 
         * @param key getter class
         * @param defaultValue warning! untyped! be careful!
         * @return getter interface with actual value, or if no value assigned, defaultValue wrapped with getter interface or null if key is wrong getter type
         */
        <T extends GetBase> T get(Class<T> key, Object defaultValue);
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
    public static <RESULT_CLASS, INITIAL_BUILDER extends Base
    > MegaBuilder<RESULT_CLASS, INITIAL_BUILDER> newMegaBuilder(
        MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> builderDefinition,
        ClassBuilder         <RESULT_CLASS                 > classBuilder
    ) {
        return new MegaBuilder<RESULT_CLASS, INITIAL_BUILDER>(
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
    public static <RESULT_CLASS, INITIAL_BUILDER extends Base
    > MegaBuilder<RESULT_CLASS, INITIAL_BUILDER> newMegaBuilder(
        Class       <INITIAL_BUILDER> initialBuilderClass,
        ClassBuilder<RESULT_CLASS   > classBuilder
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
    public static <RESULT_CLASS, INITIAL_BUILDER extends Base
    > INITIAL_BUILDER newBuilder(
        Class       <INITIAL_BUILDER> initialBuilderClass,
        ClassBuilder<RESULT_CLASS   > classBuilder
    ) {
        return newMegaBuilder(initialBuilderClass, classBuilder).create();
    }
    
    protected final MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> _definition  ;
    protected final ClassBuilder         <RESULT_CLASS                 > _classBuilder;
    
    protected Map<Class<? extends Base>, Method> _creatorMethodByBuiltClass = null;
    
    public MegaBuilder(
        MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> definition  ,
        ClassBuilder         <RESULT_CLASS                 > classBuilder
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
                    && (1 == parameterTypes.length)
                    && (Base.class.isAssignableFrom(parameterTypes[0]))
                ) {
                    final MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>.Definition<? extends Base>
                    bulderDefinition = definition.definitionByClassMap().get(parameterTypes[0]);
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
    
    public RESULT_CLASS build(BuilderInstance<? extends Base, RESULT_CLASS, INITIAL_BUILDER> instance) {
        if(null != _creatorMethodByBuiltClass) {
            final Method creator = _creatorMethodByBuiltClass.get(instance.definition().builderClass());
            if(null != creator) {
                try {
                    @SuppressWarnings("unchecked")
                    final RESULT_CLASS result = (RESULT_CLASS) creator.invoke(_classBuilder, instance.proxy());
                    return result;
                } catch(ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return _classBuilder.build((BuiltValues) instance.proxy());
    }
    
    public INITIAL_BUILDER create() { return definition().create(this); }

    public MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> definition() { return _definition; }
    
}
