package com.speakingfish.common.builder.mega;

public class MegaBuilder {
    
    public static <RESULT_CLASS, INITIAL_BUILDER extends Base>
    MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER> newDefinition(
        Class       <INITIAL_BUILDER> initialBuilderClass,
        ClassBuilder<RESULT_CLASS   > resultClassBuilder
    ) {
        return new MegaBuilderDefinition<RESULT_CLASS, INITIAL_BUILDER>(
            initialBuilderClass,
            resultClassBuilder
            );
    }

}
