package org.jenkinsci.plugins.recipe;

import hudson.util.XStream2;
import junit.framework.TestCase;
import org.jenkinsci.plugins.recipe.ingredients.Parameter;

import java.util.Arrays;

/**
 * @author Kohsuke Kawaguchi
 */
public class RecipeTest extends TestCase {
    XStream2 xs = new XStream2();

    public void test() {
        xs.alias("recipe", Recipe.class);
        xs.alias("param", Parameter.class);

        Recipe r = new Recipe("foo","1.0","foo","lorem ipsum", Arrays.<Ingredient>asList(
                new Parameter("foo"),
                new Parameter("bar")
        ));

        System.out.println(xs.toXML(r));
    }
}
