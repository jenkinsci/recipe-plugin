package org.jenkinsci.plugins.recipe;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class Ingredient extends AbstractDescribableImpl<Ingredient> implements ExtensionPoint {
    /**
     * Apply the import options to this ingredient
     * (such as parameter values, variable names, etc.)
     *
     * This is a destructive operation.
     */
    public void apply(StaplerRequest req, JSONObject opt) {
        req.bindJSON(this,opt);
    }

    /**
     * Imports this ingredient into the current Jenkins.
     * @param recipe
     */
    protected abstract void cook(Recipe recipe) throws IOException;
}
