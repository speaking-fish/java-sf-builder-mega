package com.speakingfish.common.builder.mega.test;

import com.speakingfish.common.builder.mega.BuiltBase;
import com.speakingfish.common.builder.mega.BuiltValues;
import com.speakingfish.common.builder.mega.ClassBuilder;
import com.speakingfish.common.builder.mega.GetBase;
import com.speakingfish.common.builder.mega.MegaBuilderDefinition;
import com.speakingfish.common.builder.mega.TransBase;
import com.speakingfish.common.builder.mega.MegaBuilder;
import com.speakingfish.common.builder.mega.test.MyClass.Build.*;

public class MyClass {
    
    protected final int    first ;
    protected final double second;
    protected final String third ;
    
    public static class Build {

        // Built interface
        // extends BuiltBase with result class type parameter
        public interface Built extends BuiltBase<MyClass> {
        }
        
        // Getter interface
        // 1. extends GetBase
        // 2. has single field getter method without parameters
        //
        //                    extends GetBase                field name
        //                                  |   result type  |
        //                                  |         |      |
        public interface Get_first  extends GetBase { int    first (); }
        public interface Get_second extends GetBase { double second(); }
        public interface Get_third  extends GetBase { String third (); }
    
        // Shortcuts for getter interfaces
        public interface G_1 extends Get_first {}
        public interface G_2 extends Get_second{}
        public interface G_3 extends Get_third {}
        
        // Transition interface
        // 1. extends TransBase
        // 2. has single type parameter, what extends linked getter interface
        // 3. has single transition method with parameter of same type as in linked getter interface
        //    and return type of this transition interface type parameter
        //
        //                                  return type parameter
        //                                                      | field name
        // type parameter extends linked getter                 | |
        //                             |                        | |      field type same as in linked getter
        //                             |                        | |      |
        public interface T_1<T extends G_1> extends TransBase { T first (int    first ); }
        public interface T_2<T extends G_2> extends TransBase { T second(double second); }
        public interface T_3<T extends G_3> extends TransBase { T third (String third ); }
    
        // Before make builder interfaces matrix, draw valid field combination matrix:
        //
        // for example:
        //
        // first second third
        // -     -      -
        // +     -      -
        // +     +      -
        // +     -      +
        // -     +      +
        // -     -      +
        //
        //
        
        // Builder interface
        // 1. extends getter interfaces except initial builder
        // 2. extends transition interfaces
        // 3. optionally extends Built interface
        //
        //                                              transition interfaces
        //                                                    |                       optional Built interface
        //                        getter interfaces           |                             |
        //                                   |                |                             |         matrix here
        //                             -------------  ----------------------------------  -----       ---------
        //
        //                             first          first                                           first 
        //                             |    second    |           second                              | second
        //                             |    |    third|           |           third                   | | third
        //                             |    |    |    |           |           |                       | | |
        public interface B     extends                T_1<B_1>  , T_2<B_2>  , T_3<B_3>  , Built {} // - - -
        public interface B_1   extends G_1,                       T_2<B_1_2>, T_3<B_1_3>, Built {} // + - -
        public interface B_1_2 extends G_1, G_2,                                          Built {} // + + - 
        public interface B_1_3 extends G_1,      G_3,                                     Built {} // + - +
        public interface B_2   extends      G_2,      T_1<B_1_2>,             T_3<B_2_3>        {} //
        public interface B_2_3 extends      G_2, G_3,                                     Built {} // - + +
        public interface B_3   extends           G_3, T_1<B_1_3>, T_2<B_2_3>,             Built {} // - - +
        
        public interface Builder extends B {}
    
    }
    
    protected static final MegaBuilderDefinition<MyClass, Builder> __builder = MegaBuilder.newDefinition(
        Builder.class,
        new ClassBuilder<MyClass>() {
            /**
             * Common builder
             * @param builder
             */
            @Override public MyClass build(BuiltValues values) {
                return new MyClass(
                    // There two field access methods: 
                    // 1. access field via check instanceof and cast to getter class:
                    (values instanceof Get_first) ? ((Get_first) values).first() : -1,
                  //(null == values.get(Get_first .class)) ? -1        : values.get(Get_first .class).first (),
                    // 2. access field via BuiltValues.get method
                    (null == values.get(Get_second.class)) ? Double.NaN: values.get(Get_second.class).second(),
                    (null == values.get(Get_third .class)) ? null      : values.get(Get_third .class).third ()
                    );
            }
            
            /**
             * Specific builder
             * @param builder
             */
            @SuppressWarnings("unused")
            public MyClass build(B_1_2 builder) {
                return new MyClass(
                    builder.first (),
                    builder.second(),
                    null
                    );
            }
            
        }
    );
    
    public static Builder builder() {
        return __builder.createBuilder();
    }
    
    protected MyClass(
        int    first ,
        double second,
        String third
    ) {
        super();
        this.first = first ;
        this.second= second;
        this.third = third ;
    }
    
    
    @Override public String toString() {
        return "FooClass [first=" + first + ", second=" + second + ", third=" + third + "]";
    }

    public static void main(String[] args) {
        MyClass[] values = new MyClass[] {
            builder()                              .build(),
            builder().first(1)                     .build(),
            builder().first(1).second(2)           .build(), builder().second(2  ).first (1).build(),
            builder().first(1)          .third("3").build(), builder().third ("3").first (1).build(), 
            builder()         .second(2).third("3").build(), builder().third ("3").second(2).build(),
            builder()                   .third("3").build(),
        };
        for(MyClass value : values) {
            System.out.println(value);
        }
    }

}
