package com.speakingfish.common.builder.mega;

/**
 * 
 * Facade
 *
 */
public class MegaBuilder {
    
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
        Class       <INITIAL_BUILDER> initialBuilderClass,
        ClassBuilder<RESULT_CLASS   > resultClassBuilder
    ) {
        return new MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>(
            initialBuilderClass,
            resultClassBuilder
            );
    }

}
