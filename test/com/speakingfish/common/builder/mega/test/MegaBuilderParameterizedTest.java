package com.speakingfish.common.builder.mega.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.speakingfish.common.builder.mega.MegaBuilder.BuiltValues;
import com.speakingfish.common.builder.mega.test.MyParameterizedClass.Build.Get_first;

public class MegaBuilderParameterizedTest {
    
	@Test public void testFullMatrixEquals() {
        List<MyParameterizedClass<String>> constructed = Arrays.asList(
        //  main                                                    alternatives
        //  ------------------------------------------------------  ------------------------------------------------------
            new MyParameterizedClass<String>(-1, Double.NaN, null),
            new MyParameterizedClass<String>( 1, Double.NaN, null),
            new MyParameterizedClass<String>( 1,          2, null), new MyParameterizedClass<String>( 1,          2, null),
            new MyParameterizedClass<String>( 1, Double.NaN, "3" ), new MyParameterizedClass<String>( 1, Double.NaN, "3" ), 
            new MyParameterizedClass<String>(-1,          2, "3" ), new MyParameterizedClass<String>(-1,          2, "3" ),
            new MyParameterizedClass<String>(-1, Double.NaN, "3" )
        );
        
        List<MyParameterizedClass<String>> built = Arrays.asList(
        //  main                                                                          alternatives
        //  ----------------------------------------------------------------------------  --------------------------------------------------------------------
            MyParameterizedClass.<String>builder()                              .build(),
            MyParameterizedClass.<String>builder().first(1)                     .build(),
            MyParameterizedClass.<String>builder().first(1).second(2)           .build(), MyParameterizedClass.<String>builder().second(2  ).first (1).build(),
            MyParameterizedClass.<String>builder().first(1)          .third("3").build(), MyParameterizedClass.<String>builder().third ("3").first (1).build(), 
            MyParameterizedClass.<String>builder()         .second(2).third("3").build(), MyParameterizedClass.<String>builder().third ("3").second(2).build(),
            MyParameterizedClass.<String>builder()                   .third("3").build()                       
        );                                                                                                     
        assertEquals(constructed,  built);                                                                     
	}

    @Test public void testGetters() {
        assertEquals(1, MyParameterizedClass.builder().first(1).first());
        assertTrue  (2 == MyParameterizedClass.builder().second(2).second());
        assertEquals("3", MyParameterizedClass.builder().third("3").third());
        
        assertEquals(1, MyParameterizedClass.builder().first(1).second(2).first());
        assertTrue  (2 == MyParameterizedClass.builder().second(2).first(1).second());
        assertEquals("3", MyParameterizedClass.builder().third("3").second(1).third());

        assertEquals(1, MyParameterizedClass.builder().first(1).second(2).first());
        assertTrue  (2 == MyParameterizedClass.builder().second(2).first(1).second());
        assertEquals("3", MyParameterizedClass.builder().third("3").second(1).third());
    }

    @Test public void testBuilderCasts() {
        assertTrue  (MyParameterizedClass.builder() instanceof BuiltValues);
        assertFalse (MyParameterizedClass.builder() instanceof Get_first  );
        assertEquals(null, ((BuiltValues) MyParameterizedClass.builder()).get(Get_first.class));

        assertTrue     (MyParameterizedClass.builder().first(1) instanceof BuiltValues);
        assertTrue     (MyParameterizedClass.builder().first(1) instanceof Get_first  );
        assertNotEquals(null, ((BuiltValues) MyParameterizedClass.builder().first(1)).get(Get_first.class));
        assertEquals   (1, ((Get_first) ((BuiltValues) MyParameterizedClass.builder().first(1)).get(Get_first.class)).first());
    }

    @Test (expected = ClassCastException.class) public void testCastException1() {
        assertEquals(1, ((Get_first) MyParameterizedClass.builder()).first());
    }

    @Test (expected = ClassCastException.class) public void testCastException2() {
        assertEquals(1, ((Get_first) MyParameterizedClass.builder().second(2)).first());
    }
    
}
