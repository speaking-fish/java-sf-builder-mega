package com.speakingfish.common.builder.mega.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    MegaBuilderTest             .class,
    MegaBuilderParameterizedTest.class,
})

public class AllTests {}
