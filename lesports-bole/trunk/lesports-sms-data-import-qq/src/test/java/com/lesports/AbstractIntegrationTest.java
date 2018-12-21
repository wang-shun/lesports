package com.lesports;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for integration tests adding some sample data through the MongoDB Java driver.
 *
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext*.xml"})
public abstract class AbstractIntegrationTest {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractIntegrationTest.class);


}
