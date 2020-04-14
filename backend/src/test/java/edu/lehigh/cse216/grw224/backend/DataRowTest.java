package edu.lehigh.cse216.grw224.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class DataRowTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DataRowTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DataRowTest.class);
    }

    /**
     * Ensure that the first constructor populates every field of the object it
     * creates
     */
    public void testConstructor1() {
        String content = "Test Content";
        int id = 17;
        DataRow d = new DataRow(id, content);
        assertTrue(d.mContent.equals(content));
        assertTrue(d.mId == id);
        assertTrue(d.mLikes == 0);
        assertTrue(d.mDislikes == 0);
    }
    

    /**
     * Ensure that the second constructor populates every field of the object it
     * creates
     */
    public void testConstructor2() {
        String content = "Test Content";
        int id = 17;
        DataRow d = new DataRow(id, content, 3, 4);
        assertTrue(d.mContent.equals(content));
        assertTrue(d.mId == id);
        assertTrue(d.mLikes == 3);
        assertTrue(d.mDislikes == 4);
    }

    /**
     * Ensure that the copy constructor works correctly
     */
    public void testCopyconstructor() {
        String content = "Test Content For Copy";
        int id = 177;
        DataRow d = new DataRow(id, content);
        d.mLikes = 3;
        d.mDislikes = 4;
        DataRow d2 = new DataRow(d);
        assertTrue(d2.mContent.equals(d.mContent));
        assertTrue(d2.mId == d.mId);
        assertTrue(d2.mLikes == d.mLikes);
        assertTrue(d2.mDislikes == d.mDislikes);
    }
}