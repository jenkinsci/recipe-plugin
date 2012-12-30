package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.model.DownloadService.Downloadable;
import hudson.util.VersionNumber;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Catalog of recipes retrieved from the update center.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
@ExportedBean
public class RecipeCatalog extends Downloadable {
    public RecipeCatalog() {
        super(RecipeCatalog.class.getName());
    }

    /**
     * Obtained the parsed recipes.
     */
    @Exported
    public List<CommuntiyRecipe> getRecipes() throws IOException {
        JSONObject d = getData();
        if (d==null)        return Collections.emptyList();
        return JSONArray.toList(d.getJSONArray("list"), CommuntiyRecipe.class);
    }

    public CommuntiyRecipe getRecipe(String id) throws IOException {
        for (CommuntiyRecipe r : getRecipes()) {
            if (r.id.equals(id))
                return r;
        }
        return null;
    }

    @ExportedBean
    public static class CommuntiyRecipe {
        /**
         * User ID on jenkins-ci.org representing who submitted this.
         */
        @Exported
        public String author;
        /**
         * Human readable title of this recipe in plain text.
         */
        @Exported
        public String displayName;
        /**
         * More lengthy description of the recipe in plain text.
         */
        @Exported
        public String description;
        /**
         * Unique name of the recipe that consists entirely from identifier-safe characters
         */
        @Exported
        public String id;
        /**
         * Version number of this recipe.
         */
        @Exported
        public String version;
        /**
         * When was this recipe published? In the same format as {@link Date#getTime()}.
         */
        @Exported
        public long timestamp;

        @Exported
        public String url;

        public VersionNumber parseVersion() {
            return new VersionNumber(version);
        }

        /**
         * Initiate the import session with this recipe.
         */
        public HttpResponse doImport() throws IOException {
            return ImportWizard.get().doRetrieve(new URL(url));
        }
    }
}
