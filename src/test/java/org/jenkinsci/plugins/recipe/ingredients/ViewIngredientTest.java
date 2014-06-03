package org.jenkinsci.plugins.recipe.ingredients;

import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import org.jenkinsci.plugins.recipe.ImportReportList;
import org.jenkinsci.plugins.recipe.Recipe;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

/**
 * @author Kohsuke Kawaguchi
 */
public class ViewIngredientTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void roundtrip() throws Exception {

        FreeStyleProject foo = j.createFreeStyleProject("foo");

        ListView v = new ListView("bar");
        v.add(foo);
        j.jenkins.addView(v);

        // export & import this
        ViewIngredient i = ViewIngredient.fromView(v);
        i.setName("imported-view");
        new Recipe("id", "1", "Display Name", "Description", asList(i)).cook(new ImportReportList());

        ListView nv = (ListView)j.jenkins.getView("imported-view");

        assertSame(nv.getOwner(),j.jenkins);
        assertSame(nv.getItem("foo"),foo);
    }
}
