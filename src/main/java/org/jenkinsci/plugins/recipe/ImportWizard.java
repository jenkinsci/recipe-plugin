package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.ManagementLink;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.net.URL;

/**
 * Entry point to the import wizard.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class ImportWizard extends ManagementLink implements RecipeWizard {
    @Override
    public String getIconFileName() {
        return "/plugin/recipe/images/48x48/import.png";
    }

    @Override
    public String getUrlName() {
        return "recipe-import";
    }

    public String getDisplayName() {
        return "Import Recipe";
    }

    @Override
    public String getDescription() {
        return "Import jobs, views, and so on from a recipe XML file.";
    }

    public boolean isImport() {
        return true;
    }

    public boolean isExport() {
        return false;
    }

    public Descriptor getDescriptorByName(String id) {
        return Jenkins.getInstance().getDescriptorByName(id);
    }

    public HttpResponse doRetrieve(@QueryParameter URL url) throws IOException {
        Recipe r = Recipe.load(url);
        ImportConversation ic = new ImportConversation(r);
        return HttpResponses.redirectTo("conversation");
    }

    /**
     * Binds {@link ImportConversation} to /conversation/
     */
    public ImportConversation getConversation() {
        return ImportConversation.getCurrent();
    }
}
