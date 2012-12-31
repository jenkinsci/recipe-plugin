package org.jenkinsci.plugins.recipe;

import hudson.*;
import hudson.model.Descriptor;
import hudson.model.Failure;
import hudson.model.ManagementLink;
import jenkins.model.Jenkins;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.File;
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
        // local file access is a potential security hole,
        // so require higher-level privilege.
        // other access controls are done during mutation.
        if (url.getProtocol().equals("file"))
            Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);

        return retrieve(url);
    }

    // private, because this is outside the access control
    private HttpResponse retrieve(URL url) throws IOException {
        ImportConversation ic = new ImportConversation(Recipe.load(url));
        return HttpResponses.redirectViaContextPath(getUrlName() + "/conversation");
    }

    public HttpResponse doUpload(StaplerRequest req) throws IOException, ServletException {
        try {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

            // Parse the request
            FileItem fileItem = (FileItem) upload.parseRequest(req).get(0);

            File f = File.createTempFile("uploaded", "jrcp");
            fileItem.write(f);
            fileItem.delete();

            return retrieve(f.toURI().toURL());
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {// grrr. fileItem.write throws this
            throw new ServletException(e);
        }
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
