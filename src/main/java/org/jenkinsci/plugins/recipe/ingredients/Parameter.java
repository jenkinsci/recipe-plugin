package org.jenkinsci.plugins.recipe.ingredients;

import hudson.Extension;
import org.jenkinsci.plugins.recipe.Ingredient;
import org.jenkinsci.plugins.recipe.IngredientDescriptor;
import org.jenkinsci.plugins.recipe.Recipe;

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
    public void cook(Recipe recipe) throws IOException {
    }

    @Extension
    public static class DescriptorImpl extends IngredientDescriptor {
        @Override
        public String getDisplayName() {
            return "Parameter";
        }
    }
}
