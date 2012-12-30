package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.ManagementLink;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;

/**
 * Entry point to the import wizard.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class ImportWizard extends ManagementLink implements RecipeWizard {
    @Inject
    private RecipeCatalog catalog;

    public RecipeCatalog getCatalog() {
        return catalog;
    }

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

    /**
     * Retrieves the recipe and starts the conversation to import it.
     */
    public HttpResponse doRetrieve(@QueryParameter URL url) throws IOException {
        ImportConversation ic = new ImportConversation(Recipe.load(url));
        return HttpResponses.redirectViaContextPath(getUrlName() + "/conversation");
    }

    /**
     * Binds {@link ImportConversation} to /conversation/
     */
    public ImportConversation getConversation() {
        return ImportConversation.getCurrent();
    }

    public static ImportWizard get() {
        return all().get(ImportWizard.class);
    }
}
