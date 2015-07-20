package com.speakingfish.common.builder.mega;

import com.speakingfish.common.builder.mega.impl.*;

/**
 * 
 * Facade
 *
 */
public class MegaBuilder<RESULT_CLASS, INITIAL_BUILDER extends MegaBuilder.Base> {
    
    /** marker interface */
    public interface Base {}
    
    /** getter base interface */
    public interface GetBase extends Base {}
    
    /** transition base interface */
    public interface TransBase extends Base {}
    
    /** Built base interface */
    public interface BuiltBase<T> extends Base {
        T build();
    }

    /**
     *
     * Result class creation is here
     *
     * @param <T> result class
     */
    public interface ClassBuilder<T> {
        T build(BuiltValues values);
    }
   
    /**
     * Field values holder
     *  
     * @see ClassBuilder#build(BuiltValues)
     * */
    public interface BuiltValues extends Base {
        <T extends GetBase> T get(Class<T> key);
    }
    
    public static <RESULT_CLASS, INITIAL_BUILDER extends Base
    > MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> newDefinition(
        Class<INITIAL_BUILDER> initialBuilderClass
    ) {
        return new MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>(initialBuilderClass);
    }

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

    public static <RESULT_CLASS, INITIAL_BUILDER extends Base
    > INITIAL_BUILDER newBuilder(
        Class       <INITIAL_BUILDER> initialBuilderClass,
        ClassBuilder<RESULT_CLASS   > classBuilder
    ) {
        return newMegaBuilder(
            MegaBuilder.<RESULT_CLASS, INITIAL_BUILDER>newDefinition(initialBuilderClass),
            classBuilder
            ).create();
    }
    
    protected final MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> _definition  ;
    protected final ClassBuilder         <RESULT_CLASS                 > _classBuilder;

    public MegaBuilder(
        MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> definition  ,
        ClassBuilder         <RESULT_CLASS                 > classBuilder
    ) {
        super();
        _definition   = definition  ;
        _classBuilder = classBuilder;
    }
    
    protected RESULT_CLASS build(Instance<? extends Base, RESULT_CLASS, INITIAL_BUILDER> instance) {
        return _classBuilder.build((BuiltValues) instance.proxy());
    }
    
    public INITIAL_BUILDER create() { return definition().create(this); }

    public MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> definition() { return _definition; }
    
}
