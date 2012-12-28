package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.ManagementLink;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.recipe.mechanisms.DownloadMechanism;
import org.jenkinsci.plugins.recipe.mechanisms.ExportMechanism;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class ExportWizard extends ManagementLink implements RecipeWizard, Describable<ExportWizard> {
    @Override
    public String getIconFileName() {
        return "setting.png";
    }

    @Override
    public String getUrlName() {
        return "recipe-export";
    }

    public String getDisplayName() {
        return "Export Recipe";
    }

    @Override
    public String getDescription() {
        return "Export jobs, views, and so on as an XML file so that others can import them into their Jenkins.";
    }

    public boolean isImport() {
        return false;
    }

    public boolean isExport() {
        return true;
    }

    public Descriptor getDescriptorByName(String id) {
        return Jenkins.getInstance().getDescriptorByName(id);
    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    // TODO: create a conversation scoped object and move this and transport
    public Recipe getRecipe() {
        // remember the last recipe served and start from there
        ExportMechanism t = getMechanism();
        if (t!=null)    return t.getRecipe();
        return null;
    }

    /**
     * Maps the currently selected export mechanism to the URL space.
     */
    public ExportMechanism getMechanism() {
        ExportMechanism e = (ExportMechanism) Stapler.getCurrentRequest().getSession().getAttribute(RECIPE);
        if (e==null)    e = new DownloadMechanism();    // default to the download
        return e;
    }

    public void doExport(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        Recipe recipe = req.bindJSON(Recipe.class, req.getSubmittedForm().getJSONObject("recipe"));
        ExportMechanism mechanism = req.bindJSON(ExportMechanism.class, req.getSubmittedForm().getJSONObject("mechanism"));
        mechanism.setRecipe(recipe);

        req.getSession().setAttribute(RECIPE,mechanism);
        Util.sendRedirect(rsp, HttpServletResponse.SC_SEE_OTHER,"mechanism/export");
    }

    private static final String RECIPE = ExportWizard.class.getName()+".recipe";

    @Extension
    public static class DescriptorImpl extends Descriptor<ExportWizard> {
        public String getDisplayName() {
            return "";
        }
    }
}

