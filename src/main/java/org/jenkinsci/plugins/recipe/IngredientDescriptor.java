package org.jenkinsci.plugins.recipe;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

import java.util.Locale;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class IngredientDescriptor extends Descriptor<Ingredient> {

    /**
     * Element name that appears in the persisted format.
     *
     * By default, It's "xyz" for "XyzIngredient"
     */
    public String getPersistenceElementName() {
        String s = clazz.getSimpleName().toLowerCase(Locale.ENGLISH);
        if (s.endsWith("ingredient"))
            s = s.substring(0,s.length()-"ingredient".length());
        return s;
    }

    public static DescriptorExtensionList<Ingredient,IngredientDescriptor> all() {
        return Jenkins.getInstance().getDescriptorList(Ingredient.class);
    }
}
