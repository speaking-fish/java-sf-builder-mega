package com.speakingfish.common.builder.mega.test;

import com.speakingfish.common.builder.mega.MegaBuilder;
import com.speakingfish.common.builder.mega.MegaBuilderDefinition;
import com.speakingfish.common.builder.mega.MegaBuilder.*;
import com.speakingfish.common.builder.mega.test.MyLocalClass.Build.*;

public class MyLocalClass {
    
    public static class Build {

        public interface Built extends BuiltBase<MyLocalClass> {
        }
        
        public interface Get_first  extends GetBase { int    first (); }
        public interface Get_second extends GetBase { double second(); }
        public interface Get_third  extends GetBase { String third (); }
    
        public interface G_1 extends Get_first {}
        public interface G_2 extends Get_second{}
        public interface G_3 extends Get_third {}
        
        public interface T_1<T extends G_1> extends TransBase { T first (int    first ); }
        public interface T_2<T extends G_2> extends TransBase { T second(double second); }
        public interface T_3<T extends G_3> extends TransBase { T third (String third ); }
    
        public interface B     extends                T_1<B_1  >, T_2<B_2  >, T_3<B_3  >, Built {} // - - -
        public interface B_1   extends G_1,                       T_2<B_1_2>, T_3<B_1_3>, Built {} // + - -
        public interface B_1_2 extends G_1, G_2,                                          Built {} // + + - 
        public interface B_1_3 extends G_1,      G_3,                                     Built {} // + - +
        public interface B_2   extends      G_2,      T_1<B_1_2>,             T_3<B_2_3>        {} //
        public interface B_2_3 extends      G_2, G_3,                                     Built {} // - + +
        public interface B_3   extends           G_3, T_1<B_1_3>, T_2<B_2_3>,             Built {} // - - +
        
        public interface Builder extends B {}
    
    }
    
    protected static final MegaBuilderDefinition<MyLocalClass, Builder> __definition = MegaBuilder.newDefinition(Builder.class);
            
    protected static final MegaBuilder<String, MyLocalClass, Builder> __builder = MegaBuilder.newMegaBuilder(
        __definition,
        new ClassBuilder<String, MyLocalClass>() {
            @Override public MyLocalClass build(String context, BuiltValues values) {
                return new MyLocalClass(
                    (values instanceof Get_first) ? ((Get_first) values).first() : -1,
                    (null == values.get(Get_second.class)) ? Double.NaN: values.get(Get_second.class).second(),
                    values.get(Get_third.class, null).third(),
                    context
                    );
            }
            
            @SuppressWarnings("unused")
            public MyLocalClass build(String context, B_1_2 builder) {
                System.out.println("Running specific builder B_1_2");
                return new MyLocalClass(
                    builder.first (),
                    builder.second(),
                    null,
                    context
                    );
            }
            
        }
        );
    
    public static Builder builder(final String context) { return __builder.create(context); }

    protected final int    first  ;
    protected final double second ;
    protected final String third  ;
    protected final String context;
    
    protected MyLocalClass(
        int    first  ,
        double second ,
        String third  ,
        String context
    ) {
        super();
        this.first  = first  ;
        this.second = second ;
        this.third  = third  ;
        this.context= context;
    }
    
    public int    first  () { return first  ; }
    public double second () { return second ; }
    public String third  () { return third  ; }
    public String context() { return context; }
    
    @Override public String toString() {
        return "MyLocalClass [first=" + first + ", second=" + second + ", third=" + third + ", context=" + context + "]";
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
        MyLocalClass other = (MyLocalClass) obj;
        if(first != other.first) return false;
        if(Double.doubleToLongBits(second) != Double.doubleToLongBits(other.second))
            return false;
        
        if(third == null) {
            if(other.third != null) return false;
            
        } else if(!third.equals(other.third)) return false;
        return true;
    }

    public static void main(String[] args) {
        MyLocalClass[] values = new MyLocalClass[] {
            builder("A")                              .build(),
            builder("B").first(1)                     .build(),
            builder("C").first(1).second(2)           .build(), builder("C").second(2  ).first (1).build(),
            builder("D").first(1)          .third("3").build(), builder("D").third ("3").first (1).build(), 
            builder("E")         .second(2).third("3").build(), builder("E").third ("3").second(2).build(),
            builder("F")                   .third("3").build(),
        };
        for(MyLocalClass value : values) {
            System.out.println(value);
        }
        
        final StringBuilder buildProgress = new StringBuilder();
        
        final Builder builder = MegaBuilder.newBuilder(
            __definition, null,
            new ClassBuilder<Object, MyLocalClass>() {
                @Override public MyLocalClass build(Object context, BuiltValues values) {
                    buildProgress.append("Running common builder. context: ").append(context).append(" values: ").append(values).append('\n');
                    return new MyLocalClass(
                        (values instanceof Get_first) ? ((Get_first) values).first() : -1,
                        (null == values.get(Get_second.class)) ? Double.NaN: values.get(Get_second.class).second(),
                        values.get(Get_third.class, null).third(),
                        ""
                        );
                }
                
                @SuppressWarnings("unused")
                public MyLocalClass build(Object context, B_1_2 builder) {
                    buildProgress.append("Running specific builder B_1_2. context: ").append(context).append(" builder: ").append(builder).append('\n');
                    return new MyLocalClass(
                        builder.first (),
                        builder.second(),
                        null,
                        ""
                        );
                }
                
            }
        );
        @SuppressWarnings("unused")
        MyLocalClass[] values2 = new MyLocalClass[] {
            builder                              .build(),
            builder.first(1)                     .build(),
            builder.first(1).second(2)           .build(), builder.second(2  ).first (1).build(),
            builder.first(1)          .third("3").build(), builder.third ("3").first (1).build(), 
            builder         .second(2).third("3").build(), builder.third ("3").second(2).build(),
            builder                   .third("3").build(),
        };
        System.out.println(buildProgress);
    }

}
