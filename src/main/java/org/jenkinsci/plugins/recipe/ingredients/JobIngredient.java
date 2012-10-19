package org.jenkinsci.plugins.recipe.ingredients;

import com.thoughtworks.xstream.io.xml.XppDriver;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import jenkins.util.xstream.XStreamDOM;
import jenkins.util.xstream.XStreamDOM.ConverterImpl;
import net.sf.json.JSONObject;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jenkinsci.plugins.recipe.Ingredient;
import org.jenkinsci.plugins.recipe.IngredientDescriptor;
import org.jenkinsci.plugins.recipe.Recipe;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public class JobIngredient extends Ingredient {
    private String name;
    private XStreamDOM definition;

    public JobIngredient(String name, XStreamDOM definition) {
        this.name = name;
        this.definition = definition;
    }

    @DataBoundConstructor
    public JobIngredient(String name) {
        this.name = name;
        AbstractProject i = Jenkins.getInstance().getItemByFullName(name, AbstractProject.class);
        if (i==null)
            throw new IllegalArgumentException("No such job: "+name);
        this.definition = XStreamDOM.from(i.getConfigFile().getXStream(),i);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public XStreamDOM getDefinition() {
        return definition;
    }

    @Override
    public void cook(Recipe recipe) throws IOException {
        // expansion of this is deferred
        XStreamDOM actual = recipe.createImportOptions().apply(definition);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XStreamDOM.ConverterImpl c = new ConverterImpl();
        c.marshal(actual, new XppDriver().createWriter(baos), null);

        Jenkins.getInstance().createProjectFromXML(name,new ByteArrayInputStream(baos.toByteArray()));
    }

    public static JobIngredient fromJob(Job j) {
        XStreamDOM dom = XStreamDOM.from(j.getConfigFile().getXStream(),j);
        return new JobIngredient(j.getName(),dom);
    }

    @Extension
    public static class DescriptorImpl extends IngredientDescriptor {
        @Override
        public String getDisplayName() {
            return "Job";
        }

        public AutoCompletionCandidates doAutoCompleteChildProjects(@QueryParameter String value) {
            return AutoCompletionCandidates.ofJobNames(AbstractProject.class,value,null,Jenkins.getInstance());
        }

        public FormValidation doCheckName(@QueryParameter String name) {
            AbstractProject i = Jenkins.getInstance().getItemByFullName(name, AbstractProject.class);
            if (i==null)    return FormValidation.error("No such job: "+name);
            return FormValidation.ok();
        }
    }
}
