package com.speakingfish.common.builder.mega.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.speakingfish.common.builder.mega.MegaBuilder.BuiltValues;
import com.speakingfish.common.builder.mega.test.MyLocalClass.Build.*;

public class MegaBuilderLocalTest {
    
	@Test public void testFullMatrixEquals() {
	    String context = String.valueOf(Math.random());
	    
        MyLocalClass[] constructed = new MyLocalClass[] {
        //  main                                   alternatives
        //  -------------------------------------  ---------------------------------------------------------
            new MyLocalClass(-1, Double.NaN, null, context),
            new MyLocalClass( 1, Double.NaN, null, context),
            new MyLocalClass( 1,          2, null, context), new MyLocalClass( 1,          2, null, context),
            new MyLocalClass( 1, Double.NaN, "3" , context), new MyLocalClass( 1, Double.NaN, "3" , context), 
            new MyLocalClass(-1,          2, "3" , context), new MyLocalClass(-1,          2, "3" , context),
            new MyLocalClass(-1, Double.NaN, "3" , context),
        };
        
        MyLocalClass[] built = new MyLocalClass[] {
        //  main                                                                 alternatives
        //  -------------------------------------------------------------------  -----------------------------------------------------------
            MyLocalClass.builder(context)                              .build(),
            MyLocalClass.builder(context).first(1)                     .build(),
            MyLocalClass.builder(context).first(1).second(2)           .build(), MyLocalClass.builder(context).second(2  ).first (1).build(),
            MyLocalClass.builder(context).first(1)          .third("3").build(), MyLocalClass.builder(context).third ("3").first (1).build(), 
            MyLocalClass.builder(context)         .second(2).third("3").build(), MyLocalClass.builder(context).third ("3").second(2).build(),
            MyLocalClass.builder(context)                   .third("3").build(),
        };
        assertArrayEquals(constructed,  built);
	}

    @Test public void testGetters() {
        assertEquals(1, MyLocalClass.builder("").first(1).first());
        assertTrue  (2 == MyLocalClass.builder("").second(2).second());
        assertEquals("3", MyLocalClass.builder("").third("3").third());
        
        assertEquals(1, MyLocalClass.builder("").first(1).second(2).first());
        assertTrue  (2 == MyLocalClass.builder("").second(2).first(1).second());
        assertEquals("3", MyLocalClass.builder("").third("3").second(1).third());

        assertEquals(1, MyLocalClass.builder("").first(1).second(2).first());
        assertTrue  (2 == MyLocalClass.builder("").second(2).first(1).second());
        assertEquals("3", MyLocalClass.builder("").third("3").second(1).third());
    }

    @Test public void testBuilderCasts() {
        assertTrue  (MyLocalClass.builder("") instanceof BuiltValues);
        assertFalse (MyLocalClass.builder("") instanceof Get_first  );
        assertEquals(null, ((BuiltValues) MyLocalClass.builder("")).get(Get_first.class));

        assertTrue     (MyLocalClass.builder("").first(1) instanceof BuiltValues);
        assertTrue     (MyLocalClass.builder("").first(1) instanceof Get_first  );
        assertNotEquals(null, ((BuiltValues) MyLocalClass.builder("").first(1)).get(Get_first.class));
        assertEquals   (1, ((Get_first) ((BuiltValues) MyLocalClass.builder("").first(1)).get(Get_first.class)).first());
    }

    @Test (expected = ClassCastException.class) public void testCastException1() {
        assertEquals(1, ((Get_first) MyLocalClass.builder("")).first());
    }

    @Test (expected = ClassCastException.class) public void testCastException2() {
        assertEquals(1, ((Get_first) MyLocalClass.builder("").second(2)).first());
    }

/* not implemented
    @Test public void testEqualsAndHashCode() {
        B_1 a_first1 = MyLocalClass.builder().first(1);
        B_1 b_first1 = MyLocalClass.builder().first(1);
        assertTrue(a_first1.equals(b_first1));
        assertEquals(a_first1.hashCode(), b_first1.hashCode());
        
        B_1_2 a_first1second2 = MyLocalClass.builder().first(1).second(2);
        B_1_2 b_second2first1 = MyLocalClass.builder().second(2).first(1);
        assertTrue(a_first1second2.equals(b_second2first1));
        assertEquals(a_first1second2.hashCode(), b_second2first1.hashCode());
    }
*/
    
}
