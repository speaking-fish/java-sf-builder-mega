package com.speakingfish.common.builder.mega.test;

import com.speakingfish.common.builder.mega.MegaBuilder;
import com.speakingfish.common.builder.mega.MegaBuilder.*;
import com.speakingfish.common.builder.mega.test.MyClass.Build.*;

public class MyClass {
    
    public static class Build {

        // Built interface
        // extends BuiltBase with result class type parameter
        public interface Built extends BuiltBase<MyClass> {}
        
        // Getter interface
        // (no interface name restrictions)
        // 1. extends GetBase
        // 2. has single field getter method without parameters (no method name restrictions)
        //
        //                    extends GetBase                field name
        //                                  |   result type  |
        //                                  |         |      |
        public interface Get_first  extends GetBase { int    first (); }
        public interface Get_second extends GetBase { double second(); }
        public interface Get_third  extends GetBase { String third (); }
    
        // Transition interface
        // (no interface name restrictions)
        // 1. extends TransBase
        // 2. has single type parameter, what extends corresponding getter interface
        // 3. has single transition method with parameter of same type as in corresponding getter interface
        //    and return type of this transition interface type parameter (no method name restrictions)
        //
        //                                                  return type parameter
        //                                                                      | field name
        // type parameter extends corresponding getter                          | |
        //                                      |                               | |      field type same as in corresponding getter
        //                                      |                               | |      |
        public interface Trans_first <T extends Get_first > extends TransBase { T first (int    first ); }
        public interface Trans_second<T extends Get_second> extends TransBase { T second(double second); }
        public interface Trans_third <T extends Get_third > extends TransBase { T third (String third ); }
        
        // Shortcuts for getter interfaces
        public interface G_1 extends Get_first {}
        public interface G_2 extends Get_second{}
        public interface G_3 extends Get_third {}
        
        // Shortcuts for transition interfaces
        public interface T_1<T extends G_1> extends Trans_first <T> {}
        public interface T_2<T extends G_2> extends Trans_second<T> {}
        public interface T_3<T extends G_3> extends Trans_third <T> {}
        
    
        // Validation table preparation step (can skip this).
        //
        // Draw full field combination matrix:
        //        
        // first second third
        // -     -      -
        // -     -      +
        // -     +      -
        // -     +      +
        // +     -      -
        // +     -      +
        // +     +      -
        // +     +      +
        //
        // Reorganize as tree:
        //
        // first second third
        // -     -      -    /
        // +     -      -    /+
        // +     +      -    /+/+
        // +     +      +    /+/+/+
        // +     -      +    /+/-/+
        // -     +      -    /-/+
        // -     +      +    /-/+/+
        // -     -      +    /-/-/+
        //
        // Mark valid field combination:
        //
        // for example:
        //
        // first second third
        // -     -      -    /       *  
        // +     -      -    /+      *
        // +     +      -    /+/+    *
        // +     +      +    /+/+/+
        // +     -      +    /+/-/+  *
        // -     +      -    /-/+     
        // -     +      +    /-/+/+  *
        // -     -      +    /-/-/+  *
        //
        // Do remove invalid terminal nodes and empty nodes. Repeat (if you can).
        //
        // first second third
        // -     -      -    /       *  
        // +     -      -    /+      *
        // +     +      -    /+/+    *
        // +     -      +    /+/-/+  *
        // -     +      -    /-/+     
        // -     +      +    /-/+/+  *
        // -     -      +    /-/-/+  *
        //
        
        // Builder interface
        // (no interface name restrictions)
        // 1. extends getter interfaces except initial builder
        // 2. extends transition interfaces
        // 3. optionally extends Built interface
        //
        //                                              transition interfaces
        //                                                    |                    optional Built interface
        //                        getter interfaces           |                             |
        //                                   |                |                             |         matrix here
        //                             -------------  ----------------------------------  -----       ---------
        //
        //                             first          first                                           first 
        //                             |    second    |           second                              | second
        //                             |    |    third|           |           third                   | | third
        //                             |    |    |    |           |           |                       | | |
        public interface B     extends                T_1<B_1  >, T_2<B_2  >, T_3<B_3  >, Built {} // - - -    /       *
        public interface B_1   extends G_1,                       T_2<B_1_2>, T_3<B_1_3>, Built {} // + - -    /+      *
        public interface B_1_2 extends G_1, G_2,                                          Built {} // + + -    /+/+    *
        public interface B_1_3 extends G_1,      G_3,                                     Built {} // + - +    /+/-/+  *
        public interface B_2   extends      G_2,      T_1<B_1_2>,             T_3<B_2_3>        {} // - + -    /-/+     
        public interface B_2_3 extends      G_2, G_3,                                     Built {} // - + +    /-/+/+  *
        public interface B_3   extends           G_3, T_1<B_1_3>, T_2<B_2_3>,             Built {} // - - +    /-/-/+  *

        public interface Builder extends B {}
    
    }
    
    protected static final Builder __builder = MegaBuilder.newBuilder(
        Builder.class, null,
        new ClassBuilder<Object, MyClass>() {
            /**
             * Common builder [required]
             * @param context
             * @param builder
             */
            @Override public MyClass build(Object context, BuiltValues values) {
                return new MyClass(
                    // There three ways to access values: 
                    // 1. access field via check instanceof and cast to getter class:
                    (values instanceof Get_first) ? ((Get_first) values).first() : -1,
                    // 2. access field via BuiltValues.get method
                    (null == values.get(Get_second.class)) ? Double.NaN: values.get(Get_second.class).second(),
                    // 3. access field via BuiltValues.getDefault then specify default value, then get
                    values.getDefault(Trans_third.class).third(null).third()
                    );
            }
            
            /**
             * Specific builder [optional]
             * @param context
             * @param builder
             */
            @SuppressWarnings("unused")
            public MyClass build(Object context, B_1_2 builder) {
                System.out.println("Running specific builder B_1_2 for " + MyClass.class);
                return new MyClass(
                    builder.first (),
                    builder.second(),
                    null
                    );
            }
            
        }
    );
    
    public static Builder builder() { return __builder; }
    
    protected final int    first ;
    protected final double second;
    protected final String third ;
    
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
    
    public int    first () { return first ; }
    public double second() { return second; }
    public String third () { return third ; }
    
    @Override public String toString() {
        return "MyClass [first=" + first + ", second=" + second + ", third=" + third + "]";
    }
    
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + first;
        long temp;
        temp = Double.doubleToLongBits(second);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((third == null) ? 0 : third.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        MyClass other = (MyClass) obj;
        if(first != other.first) return false;
        if(Double.doubleToLongBits(second) != Double.doubleToLongBits(other.second))
            return false;
        
        if(third == null) {
            if(other.third != null) return false;
            
        } else if(!third.equals(other.third)) return false;
        return true;
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
