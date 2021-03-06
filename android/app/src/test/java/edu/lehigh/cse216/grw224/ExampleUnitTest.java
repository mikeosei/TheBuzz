package edu.lehigh.cse216.grw224;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void Datum_constructor_sets_fields() throws Exception {
        Datum d = new Datum(7, "hello world", 10, 10);
        assertEquals(d.mId, 7);
        assertEquals(d.mContent, "hello world");
    }

    @Test
    public void CommentsDatum() throws Exception {
        CommentsDatum test = new CommentsDatum(1, 2, "test");
        assertEquals(test.mComment, "test");
        assertEquals(test.mId, 1);
    }
}