package org.jenkinsci.plugins.recipe.ingredients;

import hudson.Extension;
import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.Util;
import hudson.model.UpdateCenter;
import hudson.model.UpdateSite;
import hudson.util.IOException2;
import hudson.util.ListBoxModel;
import hudson.util.VersionNumber;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.recipe.ImportReportList;
import org.jenkinsci.plugins.recipe.Ingredient;
import org.jenkinsci.plugins.recipe.IngredientDescriptor;
import org.jenkinsci.plugins.recipe.Recipe;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Kohsuke Kawaguchi
 */
public class PluginIngredient extends Ingredient {
    public final String names;

    public PluginIngredient(String names) {
        this.names = names;
    }

    @DataBoundConstructor
    public PluginIngredient(List<String> names) {
        this.names = Util.join(names, " ");
    }

    public List<String> getNameList() {
        return Arrays.asList(names.split("[, \t]+"));
    }

    /**
     * Parses all the specific plugins and return their information.
     */
    public List<Item> parse() {
        List<Item> r = new ArrayList<Item>();
        for (String name : getNameList()) {
            r.add(new Item(name));
        }
        return r;
    }

    @Override
    protected void cook(Recipe recipe, ImportReportList reportList) throws IOException, InterruptedException {

        for (Item i : parse()) {
            i.cook(reportList);
        }

        PluginManager pm = Jenkins.getInstance().pluginManager;
        UpdateCenter uc = Jenkins.getInstance().getUpdateCenter();

        for (String name : getNameList()) {
            String version=null;
            int idx =name.lastIndexOf('@');
            if (idx>=0) {
                version = name.substring(idx+1);
                name = name.substring(0,idx);
            }

            try {
                UpdateSite.Plugin p = uc.getPlugin(name);
                PluginWrapper cur = pm.getPlugin(name);
                if (cur==null) {
                    p.deploy(true).get();
                } else {
                    if (version!=null) {
                        if (new VersionNumber(version).compareTo(cur.getVersionNumber())>0) {
                            p.deploy(true).get();
                        } else {
                            // already have up-to-date version
                        }
                    } else {
                        // some version is present in this system, but there's also an update
                        // there can be many valid strategies, but let's stick with what we have
                        // TODO: maybe warn the user if there's an update?
                    }
                }
            } catch (ExecutionException e) {
                throw new IOException2("Failed to install plugin: "+name,e);
            }
        }
    }

    /**
     * Represents a reference to single plugin in the recipe and how it compares with the current state of
     * the system.
     */
    public static final class Item {
        /**
         * Short Name of the plugin as in {@link PluginWrapper#getShortName()}
         */
        public final String name;
        /**
         * The version requested in the recipe, if specified.
         */
        public final VersionNumber version;
        /**
         * If this plugin is already in the current Jenkins, reference to it.
         */
        public final PluginWrapper current;
        /**
         * If this plugin is installable from the update center, reference to it.
         */
        public final UpdateSite.Plugin installSource;

        /**
         * @param token
         *      NAME[@VERSION] format used in the configuration files.
         */
        public Item(String token) {
            int idx =token.lastIndexOf('@');
            if (idx>=0) {
                this.name = token.substring(0,idx);
                this.version = new VersionNumber(token.substring(idx+1));
            } else {
                this.name = token;
                this.version = null;
            }
            PluginManager pm = Jenkins.getInstance().pluginManager;
            UpdateCenter uc = Jenkins.getInstance().getUpdateCenter();

            this.installSource = uc.getPlugin(name);
            this.current = pm.getPlugin(name);
        }

        public void cook(ImportReportList reportList) throws IOException2, InterruptedException {
            try {
                if (current==null) {
                    if (installSource!=null) {
                        installSource.deploy(true).get();
                    } else {
                        // not installable
                    }
                } else {
                    if (version!=null) {
                        if (!isUpToDate()) {
                            installSource.deploy(true).get();
                        } else {
                            // already have up-to-date version
                        }
                    } else {
                        // some version is present in this system, but there's also an update
                        // there can be many valid strategies, but let's stick with what we have
                        // TODO: maybe warn the user if there's an update?
                    }
                }
            } catch (ExecutionException e) {
                throw new IOException2("Failed to install plugin: "+name,e);
            }
        }

        /**
         * Returns true if the plugin is already installed on the system and satisfies the constraint.
         */
        public boolean isUpToDate() {
            if (current==null)      return false;   // if there's no existing version, clearly not up to date
            if (version==null)      return true;    // if a specific version is not specified, any version is considered up-to-date
            return version.compareTo(current.getVersionNumber())<=0;
        }
    }

    @Extension
    public static class DescriptorImpl extends IngredientDescriptor {
        @Override
        public String getDisplayName() {
            return "Plugin";
        }

        public ListBoxModel doFillNamesItems() {
            ListBoxModel r = new ListBoxModel();
            for (PluginWrapper p : Jenkins.getInstance().pluginManager.getPlugins())
                r.add(p.getShortName());
            return r;
        }
    }
}
