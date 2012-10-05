package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.model.ManagementLink;
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
public class ImportWizard extends ManagementLink {
    @Override
    public String getIconFileName() {
        return "setting.png";
    }

    @Override
    public String getUrlName() {
        return "recipe-import";
    }

    public String getDisplayName() {
        return "Import Recipe";
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
