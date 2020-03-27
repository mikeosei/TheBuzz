package edu.lehigh.cse216.grw224.backend;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final int HTTP_CODE_NOT_FOUND = 404;
  private static final String REASON_PHRASE_NOT_FOUND = "NOT FOUND";
/**
 * Tests {@link GoogleJsonResponseExceptionFactoryTesting}
 *
 * @author Mike Osei
 */
  public void testCreateException() throws IOException {
    GoogleJsonResponseException exception =
        GoogleJsonResponseExceptionFactoryTesting.newMock(
            JSON_FACTORY, HTTP_CODE_NOT_FOUND, REASON_PHRASE_NOT_FOUND);
    assertEquals(HTTP_CODE_NOT_FOUND, exception.getStatusCode());
    assertEquals(REASON_PHRASE_NOT_FOUND, exception.getStatusMessage());
  }
}
