package org.jenkinsci.plugins.recipe.ingredients;

import org.jenkinsci.plugins.recipe.ImportOptions;
import org.jenkinsci.plugins.recipe.Ingredient;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public class Parameter extends Ingredient {
    public String name;

    public Parameter(String name) {
        this.name = name;
    }

    @Override
    public Parameter apply(ImportOptions opts) {
        return this; // no transformation necessary
    }

    @Override
    public void cook() throws IOException {
    }
}
