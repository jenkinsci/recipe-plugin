package org.jenkinsci.plugins.recipe;

import hudson.model.DescriptorByNameOwner;

/**
 * Marker interface between {@link ImportWizard} and {@link ExportWizard}.
 * Useful in {@link Ingredient} form validation method to change behaviour.
 *
 * @author Kohsuke Kawaguchi
 */
public interface RecipeWizard extends DescriptorByNameOwner {
    boolean isImport();
    boolean isExport();
}
