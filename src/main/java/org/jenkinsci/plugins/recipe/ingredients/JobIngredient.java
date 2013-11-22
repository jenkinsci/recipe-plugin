package org.jenkinsci.plugins.recipe.ingredients;

import com.thoughtworks.xstream.io.xml.XppDriver;
import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import jenkins.util.xstream.XStreamDOM;
import jenkins.util.xstream.XStreamDOM.ConverterImpl;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jenkinsci.plugins.recipe.ImportReport;
import org.jenkinsci.plugins.recipe.ImportReportList;
import org.jenkinsci.plugins.recipe.Ingredient;
import org.jenkinsci.plugins.recipe.IngredientDescriptor;
import org.jenkinsci.plugins.recipe.Recipe;
import org.jenkinsci.plugins.recipe.RecipeWizard;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import jenkins.model.ModifiableTopLevelItemGroup;

/**
 * {@link Ingredient} that transports a job definition
 * (but no build records.)
 *
 * @author Kohsuke Kawaguchi
 */
public class JobIngredient extends Ingredient {
    /**
     * Job name.
     */
    private String name;
    /**
     * Human readable text that explains what this job is.
     */
    private String description;

    private XStreamDOM definition;

    public JobIngredient(String name, String description, XStreamDOM definition) {
        this.name = name;
        this.description = description;
        this.definition = definition;
    }

    @DataBoundConstructor
    public JobIngredient(String name, String description) {
        this.name = name;
        this.description = description;
        AbstractProject i = Jenkins.getInstance().getItemByFullName(name, AbstractProject.class);
        if (i==null)
            throw new IllegalArgumentException("No such job: "+name);
        this.definition = XStreamDOM.from(i.getConfigFile().getXStream(),i);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public XStreamDOM getDefinition() {
        return definition;
    }

    @Override
    public void cook(Recipe recipe, ImportReportList reportList) throws IOException {
        // expansion of this is deferred
        XStreamDOM actual = recipe.createImportOptions().apply(definition);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XStreamDOM.ConverterImpl c = new ConverterImpl();
        c.marshal(actual, new XppDriver().createWriter(baos), null);

        ModifiableTopLevelItemGroup g = Jenkins.getInstance();
        TopLevelItem j = g.getItem(name);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        if (j == null) {
            j = g.createProjectFromXML(name, is);
        } else if (j instanceof AbstractItem) {
            Source source = new StreamSource(is);
            ((AbstractItem) j).updateByXml(source);
        } else {
            throw new IOException("Cannot update " + j + " in place");
        }
        reportList.add(new ImportReportImpl(j));
    }

    public static JobIngredient fromJob(Job j, String description) {
        XStreamDOM dom = XStreamDOM.from(j.getConfigFile().getXStream(),j);
        return new JobIngredient(j.getName(),description, dom);
    }

    public static class ImportReportImpl extends ImportReport {
        public final TopLevelItem job;
        // TODO should have a flag for updated existing job so we can say “Updated job” rather than “Created job”

        public ImportReportImpl(TopLevelItem job) {
            this.job = job;
        }
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

        public FormValidation doCheckName(@QueryParameter String name, @AncestorInPath RecipeWizard wizard) {
            AbstractProject i = Jenkins.getInstance().getItemByFullName(name, AbstractProject.class);
            if (wizard.isExport()) {
                if (i==null)    return FormValidation.error("No such job: "+name);
            } else {
                if (i != null) {
                    return FormValidation.warning("You already have a job named " + name + "; its configuration will be overwritten (but history retained)");
                }
            }
            return FormValidation.ok();
        }
    }
}
