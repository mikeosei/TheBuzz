package edu.lehigh.cse216.grw224.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName ) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue( true );
    }

    /**
     * Test the getIntFromEnv Method
     */
    public void testGetIntFromEnv() {
        int a = App.getIntFromEnv("PORT", 4567);
        assertEquals(4567, a);
    }


    /**
     * Test the lehighEmailCheck Method
     */
    public void testLehighEmailCheck() {   
        String email = "mbo221@lehigh.edu";
        assertTrue(App.lehighEmailCheck(email));
    }

}
