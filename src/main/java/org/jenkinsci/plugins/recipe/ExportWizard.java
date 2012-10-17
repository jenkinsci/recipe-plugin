package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.model.ManagementLink;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class ExportWizard extends ManagementLink {
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

    public void doExport(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        Recipe recipe = req.bindJSON(Recipe.class, req.getSubmittedForm());
        System.out.println(recipe);
        rsp.setContentType("application/xml;charset=UTF-8");
        Recipe.XSTREAM.toXML(recipe,rsp.getOutputStream());
    }
}

