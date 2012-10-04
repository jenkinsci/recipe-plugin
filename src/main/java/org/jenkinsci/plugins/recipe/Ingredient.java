package org.jenkinsci.plugins.recipe;

import hudson.ExtensionPoint;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class Ingredient implements ExtensionPoint {
    /**
     * Apply the import options to this ingredient
     * (such as variable expansions, etc.)
     */
    public abstract Ingredient apply(ImportOptions opts);

    /**
     * Imports this ingredient into the current Jenkins.
     */
    public abstract void cook() throws IOException;
}
