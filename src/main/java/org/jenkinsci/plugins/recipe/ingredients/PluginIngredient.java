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

    @Override
    protected void cook(Recipe recipe, ImportReportList reportList) throws IOException, InterruptedException {
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
