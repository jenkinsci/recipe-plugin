package org.jenkinsci.plugins.recipe.ingredients;

import org.jenkinsci.plugins.recipe.Ingredient;
import org.jenkinsci.plugins.recipe.Recipe;
import org.jenkinsci.plugins.recipe.Recipe.ImportOptions;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public class Parameter extends Ingredient {
    public final String name;
    private String value;

    public Parameter(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void apply(ImportOptions opts) {
    }

    @Override
    public void cook(Recipe recipe) throws IOException {
    }
}
