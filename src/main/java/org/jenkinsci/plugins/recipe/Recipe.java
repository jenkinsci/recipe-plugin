package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.XStream2;
import org.jenkinsci.plugins.recipe.ingredients.Parameter;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class Recipe extends AbstractDescribableImpl<Recipe> {
    private String version;
    private String title;
    private String description;
    private List<Ingredient> ingredients = new ArrayList<Ingredient>();

    @DataBoundConstructor
    public Recipe(String version, String title, String description, List<Ingredient> ingredients) {
        this.version = version;
        this.title = title;
        this.description = description;
        this.ingredients.addAll(ingredients);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Parameter> getParameters() {
        return Util.filter(ingredients,Parameter.class);
    }

    public List<Ingredient> apply(ImportOptions opts) {
        List<Ingredient> r = new ArrayList<Ingredient>(ingredients.size());
        for (Ingredient i : ingredients)
            r.add(i.apply(opts));
        return r;
    }

    /**
     * Loads the recipe from URL.
     */
    public static Recipe load(URL url) throws IOException {
        return (Recipe)XSTREAM.fromXML(url.openStream());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Recipe> {
        @Override
        public String getDisplayName() {
            return "";
        }
    }

    public static XStream2 XSTREAM = new XStream2();

    static {
        XSTREAM.alias("recipe",Recipe.class);
    }
}
