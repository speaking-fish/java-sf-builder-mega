package com.speakingfish.common.builder.mega.test;

import java.util.List;

import com.speakingfish.common.builder.mega.MegaBuilder;
import com.speakingfish.common.builder.mega.MegaBuilder.*;
import com.speakingfish.common.builder.mega.test.MyParameterizedClass.Build.Builder;
import com.speakingfish.common.builder.mega.test.MyParameterizedClass.Build.*;

import static java.util.Arrays.*;

public class MyParameterizedClass<THIRD> {
    
    public static class Build<THIRD> {

        public interface Built<THIRD> extends BuiltBase<MyParameterizedClass<THIRD>> {
        }
        
        public interface Get_first         extends GetBase { int    first (); }
        public interface Get_second        extends GetBase { double second(); }
        public interface Get_third <THIRD> extends GetBase { THIRD  third (); }
    
        public interface G_1     extends Get_first      {}
        public interface G_2     extends Get_second     {}
        public interface G_3<P3> extends Get_third <P3> {}
        
        public interface T_1<    T extends G_1    > extends TransBase { T first (int    first ); }
        public interface T_2<    T extends G_2    > extends TransBase { T second(double second); }
        public interface T_3<P3, T extends G_3<P3>> extends TransBase { T third (P3     third ); }
    
        public interface B    <P3> extends                    T_1<B_1  <P3>>, T_2<B_2  <P3>>, T_3<P3, B_3  <P3>>, Built<P3> {}
        public interface B_1  <P3> extends G_1,                               T_2<B_1_2<P3>>, T_3<P3, B_1_3<P3>>, Built<P3> {}
        public interface B_1_2<P3> extends G_1, G_2,                                                              Built<P3> {} 
        public interface B_1_3<P3> extends G_1,      G_3<P3>,                                                     Built<P3> {}
        public interface B_2  <P3> extends      G_2,          T_1<B_1_2<P3>>,                 T_3<P3, B_2_3<P3>>            {}
        public interface B_2_3<P3> extends      G_2, G_3<P3>,                                                     Built<P3> {}
        public interface B_3  <P3> extends           G_3<P3>, T_1<B_1_3<P3>>, T_2<B_2_3<P3>>,                     Built<P3> {}
                                              
        public interface Builder<THIRD> extends B<THIRD> {}
    
    }
    
    protected static final Builder<?> __builder = MegaBuilder.newBuilder(
        Builder.class, null,
        new ClassBuilder<Object, MyParameterizedClass<?>>() {
            @Override public MyParameterizedClass<?> build(Object context, BuiltValues values) {
                return new MyParameterizedClass<Object>(
                    (values instanceof Get_first) ? ((Get_first) values).first() : -1,
                    (null == values.get(Get_second.class)) ? Double.NaN: values.get(Get_second.class).second(),
                    values.get(Get_third.class, null).third()
                    );
            }
            
            @SuppressWarnings("unused")
            public <THIRD> MyParameterizedClass<THIRD> build(Object context, B_1_2<THIRD> builder) {
                System.out.println("Running specific builder B_1_2");
                return new MyParameterizedClass<THIRD>(
                    builder.first (),
                    builder.second(),
                    null
                    );
            }
            
        }
    );
    
    @SuppressWarnings("unchecked")
    public static <THIRD> Builder<THIRD> builder() { return (Builder<THIRD>) __builder; }
    
    protected final int    first ;
    protected final double second;
    protected final THIRD  third ;
    
    protected MyParameterizedClass(
        int    first ,
        double second,
        THIRD  third
    ) {
        super();
        this.first = first ;
        this.second= second;
        this.third = third ;
    }
    
    public int    first () { return first ; }
    public double second() { return second; }
    public THIRD  third () { return third ; }
    
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
        MyParameterizedClass<?> other = (MyParameterizedClass<?>) obj;
        if(first != other.first) return false;
        if(Double.doubleToLongBits(second) != Double.doubleToLongBits(other.second))
            return false;
        
        if(third == null) {
            if(other.third != null) return false;
            
        } else if(!third.equals(other.third)) return false;
        return true;
    }

    public static void main(String[] args) {
        List<MyParameterizedClass<List<String>>> values = asList(
            MyParameterizedClass.<List<String>>builder()                                              .build(),
            MyParameterizedClass.<List<String>>builder().first(1)                                     .build(),
            MyParameterizedClass.<List<String>>builder().first(1).second(2)                           .build(), MyParameterizedClass.<List<String>>builder().second(2).first(1).build(),
            MyParameterizedClass.<List<String>>builder().first(1)          .third(asList("3","4","5")).build(), MyParameterizedClass.<List<String>>builder().third(asList("3","4","5")).first (1).build(), 
            MyParameterizedClass.<List<String>>builder()         .second(2).third(asList("3","4","5")).build(), MyParameterizedClass.<List<String>>builder().third(asList("3","4","5")).second(2).build(),
            MyParameterizedClass.<List<String>>builder()                   .third(asList("3","4","5")).build()
        );
        for(MyParameterizedClass<?> value : values) {
            System.out.println(value);
        }
    }

}
