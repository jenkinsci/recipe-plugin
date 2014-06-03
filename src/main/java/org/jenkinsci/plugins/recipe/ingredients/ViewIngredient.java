package org.jenkinsci.plugins.recipe.ingredients;

import hudson.Extension;
import hudson.model.View;
import jenkins.model.Jenkins;
import jenkins.util.xstream.XStreamDOM;
import org.jenkinsci.plugins.recipe.ImportReportList;
import org.jenkinsci.plugins.recipe.Ingredient;
import org.jenkinsci.plugins.recipe.IngredientDescriptor;
import org.jenkinsci.plugins.recipe.Recipe;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

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
        this.definition = parse(v);
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
        actual.getChildren().add(new XStreamDOM("name", Collections.<String,String>emptyMap(),name));

        View v = (View)Jenkins.XSTREAM2.unmarshal(actual.newReader());
        Jenkins.getInstance().addView(v);
    }

    public static ViewIngredient fromView(View v) {
        XStreamDOM dom = parse(v);
        return new ViewIngredient(v.getViewName(),dom);
    }

    /**
     * Builds DOM from a view and remove unwanted portions.
     */
    private static XStreamDOM parse(View v) {
        XStreamDOM dom = XStreamDOM.from(Jenkins.XSTREAM2,v);
        for (Iterator<XStreamDOM> itr = dom.getChildren().iterator(); itr.hasNext(); ) {
            XStreamDOM c =  itr.next();
            if (c.getTagName().equals("owner")
            ||  c.getTagName().equals("name")) {
                itr.remove();
            }
        }
        return dom;
    }

    @Extension
    public static class DescriptorImpl extends IngredientDescriptor {
        @Override
        public String getDisplayName() {
            return "View";
        }
    }
}
