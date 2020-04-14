package edu.lehigh.cse216.grw224.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.google.api.services.drive.model.File;


/**
 * Unit tests for DriveQuickstart class
 */
public class DriveQuickstartTest extends TestCase {

    /**
     * Create the test case
     * @param testName name of the test case
     */
    public DriveQuickstartTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DriveQuickstartTest.class);
    }

    public void test() {
        assertTrue(true);
    }

    /**
     * Test getDrive method
     */
    public void testGetDrive() {
        boolean test = DriveQuickstart.setup();
        if (test) {
            assertNotNull(DriveQuickstart.getDrive());
        }
    }

    /**
     * Test getNetHttpTransport method
     */
    public void testGetHttpTransport() {
        boolean test = DriveQuickstart.setup();
        if (test) {
            assertNotNull(DriveQuickstart.getNetHttpTransport());
        }
    }

    /**
     * Test insertFile method
     */
    public void testInsertFile() {
        boolean test = DriveQuickstart.setup();
        if (test) {
            java.io.File file = new java.io.File("dog.png");
            assertNotNull(DriveQuickstart.insertFile(DriveQuickstart.getDrive(), "dog.png", file, "image/png"));
        }
    }

    /**
     * Test getFile method
     */
    public void testGetFile() {
        boolean test = DriveQuickstart.setup();
        if (test) {
            java.io.File file = new java.io.File("dog.png");
            assertNotNull(DriveQuickstart.insertFile(DriveQuickstart.getDrive(), "dog.png", file, "image/png"));
            assertNotNull(DriveQuickstart.getFile(DriveQuickstart.getDrive(), "dog.png"));
        }
    }

}