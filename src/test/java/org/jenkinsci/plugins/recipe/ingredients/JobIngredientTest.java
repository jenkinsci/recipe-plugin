package org.jenkinsci.plugins.recipe.ingredients;

import hudson.model.FreeStyleProject;
import hudson.util.FormValidation;
import java.util.Collections;
import org.jenkinsci.plugins.recipe.ImportReportList;
import org.jenkinsci.plugins.recipe.ImportWizard;
import org.jenkinsci.plugins.recipe.Ingredient;
import org.jenkinsci.plugins.recipe.Recipe;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.JenkinsRule;

public class JobIngredientTest {

    @Rule public JenkinsRule r = new JenkinsRule();

    @SuppressWarnings("ThrowableResultIgnored")
    @Bug(20727)
    @Test public void reimport() throws Exception {
        FreeStyleProject prototype = r.createFreeStyleProject("prototype");
        prototype.setDescription("initial");
        JobIngredient ji = JobIngredient.fromJob(prototype, "A simple job");
        ji.setName("copy");
        assertEquals(FormValidation.Kind.OK, r.jenkins.getDescriptorByType(JobIngredient.DescriptorImpl.class).doCheckName("copy", ImportWizard.get()).kind);
        new Recipe("test", "1", "Test", "Test", Collections.<Ingredient>singletonList(ji)).cook(new ImportReportList());
        FreeStyleProject copy = r.jenkins.getItemByFullName("copy", FreeStyleProject.class);
        assertNotNull(copy);
        assertEquals("initial", copy.getDescription());
        r.assertBuildStatusSuccess(copy.scheduleBuild2(0));
        assertEquals(1, copy.getLastBuild().number);
        prototype.setDescription("edited");
        ji = JobIngredient.fromJob(prototype, "A simple job");
        ji.setName("copy");
        assertEquals(FormValidation.Kind.WARNING, r.jenkins.getDescriptorByType(JobIngredient.DescriptorImpl.class).doCheckName("copy", ImportWizard.get()).kind);
        new Recipe("test", "2", "Test", "Test", Collections.<Ingredient>singletonList(ji)).cook(new ImportReportList());
        assertEquals(copy, r.jenkins.getItemByFullName("copy", FreeStyleProject.class));
        assertEquals("edited", copy.getDescription());
        assertEquals(1, copy.getLastBuild().number);
    }

}
