package org.jenkinsci.plugins.recipe.mechanisms;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import org.jenkinsci.plugins.recipe.Recipe;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * The code that exports a recipe to somewhere.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ExportMechanism extends AbstractDescribableImpl<ExportMechanism> implements ExtensionPoint {

    private transient Recipe recipe;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Once the user selected the export mechanism, this method is called
     * to perform the export operation.
     */
    public abstract HttpResponse doExport(StaplerRequest req) throws IOException;

    @Override
    public ExportMechanismDescriptor getDescriptor() {
        return (ExportMechanismDescriptor) super.getDescriptor();
    }
}
