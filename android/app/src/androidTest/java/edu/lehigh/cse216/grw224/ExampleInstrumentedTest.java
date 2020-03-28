package edu.lehigh.cse216.grw224;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("edu.lehigh.cse216.grw224", appContext.getPackageName());
    }

    public void Datum_constructor_sets_fields() throws Exception {
        Datum d = new Datum(7, "hello world", 0,0);
        assertEquals(d.mId, 7);
        assertEquals(d.mContent, "hello world");
        assertEquals(d.mLikes, 0);
        assertEquals(d.mDislikes, 0);

    }
}
