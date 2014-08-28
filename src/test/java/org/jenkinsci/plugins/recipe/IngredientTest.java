package org.jenkinsci.plugins.recipe;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collections;
import jenkins.util.xstream.XStreamDOM;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.BeforeClass;
import org.jvnet.hudson.test.Bug;

public class IngredientTest {

    @BeforeClass public static void setDefaultCharset() {
        /* This does not work:
        System.setProperty("file.encoding", "ISO_8859-1");
        */
        try {
            Field f = Charset.class.getDeclaredField("defaultCharset");
            f.setAccessible(true);
            f.set(null, Charset.forName("ISO_8859-1"));
        } catch (Exception x) {
            assumeNoException(x);
        }
        assumeThat(Charset.defaultCharset().aliases(), hasItem("ISO_8859-1"));
    }

    @Bug(22241)
    @Test public void read() throws Exception {
        String text = "Русский мост";
        XStreamDOM initial = new XStreamDOM("x", Collections.<String,String>emptyMap(), text);
        XStreamDOM loaded = XStreamDOM.from(Ingredient.read(initial));
        assertEquals(text, loaded.getValue());
    }

}