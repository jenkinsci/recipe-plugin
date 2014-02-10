package org.jenkinsci.plugins.recipe.ingredients;

import hudson.model.View;
import java.io.IOException;
import jenkins.model.Jenkins;
import jenkins.util.xstream.XStreamDOM;
import org.jenkinsci.plugins.recipe.ImportReportList;
import org.jenkinsci.plugins.recipe.Ingredient;
import org.jenkinsci.plugins.recipe.IngredientDescriptor;
import org.jenkinsci.plugins.recipe.Recipe;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * View.
 *
 * @author Kohsuke Kawaguchi
 */
public class ViewIngredient extends Ingredient {
    private String name;
    private XStreamDOM definition;

    public ViewIngredient(String name, XStreamDOM definition) {
        this.name = name;
        this.definition = definition;
    }

    @DataBoundConstructor public ViewIngredient(String name) {
        this.name = name;
        View v = Jenkins.getInstance().getView(name); // TODO handle views of folders
        if (v == null) {
            throw new IllegalArgumentException("no such view " + name);
        }
        this.definition = XStreamDOM.from(Jenkins.XSTREAM2, v);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public XStreamDOM getDefinition() {
        return definition;
    }

    @Override
    public void cook(Recipe recipe, ImportReportList reportList) throws IOException {
        XStreamDOM actual = recipe.createImportOptions().apply(definition);

        View v = (View)Jenkins.XSTREAM2.unmarshal(actual.newReader());
        Jenkins.getInstance().addView(v);
    }

    public static ViewIngredient fromView(View v) {
        XStreamDOM dom = XStreamDOM.from(Jenkins.XSTREAM2,v);
        // TODO: remove View.owner from DOM
        return new ViewIngredient(v.getViewName(),dom);
    }

    /* TODO does not work: views are kept in main config.xml, so exported form includes <owner> (and <name>):
    @Extension
    */
    public static class DescriptorImpl extends IngredientDescriptor {
        @Override
        public String getDisplayName() {
            return "View";
        }
    }
}
