package com.hmt.oauth.passport.common;

import org.junit.runner.RunWith;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring-core.xml"})
@TransactionConfiguration(defaultRollback = false)
@ActiveProfiles(value = "develop")
public abstract class SpringTxTestCase extends AbstractJUnit4SpringContextTests {
    @org.junit.Before
    public void init(){
        Environment env = applicationContext.getEnvironment();
        String profile = env.getActiveProfiles()[0];
        System.setProperty("spring.profiles.active",profile);
    }
}