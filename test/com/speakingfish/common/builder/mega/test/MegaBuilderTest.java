package com.speakingfish.common.builder.mega.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.speakingfish.common.builder.mega.MegaBuilder.BuiltValues;
import com.speakingfish.common.builder.mega.test.MyClass.Build.Get_first;

public class MegaBuilderTest {
    
	@Test public void testFullMatrixEquals() {
        MyClass[] constructed = new MyClass[] {
        //  main                               alternatives
        //  ---------------------------------  ---------------------------------
            new MyClass(-1, Double.NaN, null),
            new MyClass( 1, Double.NaN, null),
            new MyClass( 1,          2, null), new MyClass( 1,          2, null),
            new MyClass( 1, Double.NaN, "3" ), new MyClass( 1, Double.NaN, "3" ), 
            new MyClass(-1,          2, "3" ), new MyClass(-1,          2, "3" ),
            new MyClass(-1, Double.NaN, "3" ),
        };
        
        MyClass[] built = new MyClass[] {
        //  main                                                     alternatives
        //  -------------------------------------------------------  -----------------------------------------------
            MyClass.builder()                              .build(),
            MyClass.builder().first(1)                     .build(),
            MyClass.builder().first(1).second(2)           .build(), MyClass.builder().second(2  ).first (1).build(),
            MyClass.builder().first(1)          .third("3").build(), MyClass.builder().third ("3").first (1).build(), 
            MyClass.builder()         .second(2).third("3").build(), MyClass.builder().third ("3").second(2).build(),
            MyClass.builder()                   .third("3").build(),
        };
        assertArrayEquals(constructed,  built);
	}

    @Test public void testGetters() {
        assertEquals(1, MyClass.builder().first(1).first());
        assertTrue  (2 == MyClass.builder().second(2).second());
        assertEquals("3", MyClass.builder().third("3").third());
        
        assertEquals(1, MyClass.builder().first(1).second(2).first());
        assertTrue  (2 == MyClass.builder().second(2).first(1).second());
        assertEquals("3", MyClass.builder().third("3").second(1).third());

        assertEquals(1, MyClass.builder().first(1).second(2).first());
        assertTrue  (2 == MyClass.builder().second(2).first(1).second());
        assertEquals("3", MyClass.builder().third("3").second(1).third());
    }

    @Test public void testBuilderCasts() {
        assertTrue  (MyClass.builder() instanceof BuiltValues);
        assertFalse (MyClass.builder() instanceof Get_first  );
        assertEquals(null, ((BuiltValues) MyClass.builder()).get(Get_first.class));

        assertTrue     (MyClass.builder().first(1) instanceof BuiltValues);
        assertTrue     (MyClass.builder().first(1) instanceof Get_first  );
        assertNotEquals(null, ((BuiltValues) MyClass.builder().first(1)).get(Get_first.class));
        assertEquals   (1, ((Get_first) ((BuiltValues) MyClass.builder().first(1)).get(Get_first.class)).first());
    }

    @Test (expected = ClassCastException.class) public void testCastException1() {
        assertEquals(1, ((Get_first) MyClass.builder()).first());
    }

    @Test (expected = ClassCastException.class) public void testCastException2() {
        assertEquals(1, ((Get_first) MyClass.builder().second(2)).first());
    }
    
}
