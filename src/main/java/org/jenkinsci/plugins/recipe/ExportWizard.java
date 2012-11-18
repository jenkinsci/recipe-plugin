package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.ManagementLink;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class ExportWizard extends ManagementLink implements RecipeWizard {
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

    public boolean isImport() {
        return false;
    }

    public boolean isExport() {
        return true;
    }

    public Descriptor getDescriptorByName(String id) {
        return Jenkins.getInstance().getDescriptorByName(id);
    }

    public void doExport(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        Recipe recipe = req.bindJSON(Recipe.class, req.getSubmittedForm());
        System.out.println(recipe);
        rsp.setContentType("application/xml;charset=UTF-8");
        Recipe.XSTREAM.toXML(recipe,rsp.getOutputStream());
    }
}

