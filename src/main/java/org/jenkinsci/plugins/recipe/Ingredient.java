package org.jenkinsci.plugins.recipe;

import com.thoughtworks.xstream.io.xml.XppDriver;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import jenkins.util.xstream.XStreamDOM;
import jenkins.util.xstream.XStreamDOM.ConverterImpl;
import net.sf.json.JSONObject;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.kohsuke.stapler.StaplerRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An unit of a configured item in Jenkins that gets transported to another Jenkins instance.
 *
 * {@link Recipe} is a collection of {@link Ingredient}s.
 *
 * @author Kohsuke Kawaguchi
 * @see Recipe
 */
public abstract class Ingredient extends AbstractDescribableImpl<Ingredient> implements ExtensionPoint {
    @Override
    public IngredientDescriptor getDescriptor() {
        return (IngredientDescriptor)super.getDescriptor();
    }

    /**
     * Return false if this recipe needs to be hidden during the import conversation.
     */
    public boolean isVisibleDuringImport() {
        return true;
    }

    /**
     * Apply the import options to this ingredient
     * (such as parameter values, variable names, etc.)
     *
     * This is a destructive operation.
     */
    public void apply(StaplerRequest req, JSONObject opt) {
        req.bindJSON(this,opt);
    }

    /**
     * Imports this ingredient into the current Jenkins.
     *
     * @param recipe
     * @param reportList
     *      Report what was actually done by adding {@link ImportReport} to this
     *      (both success and non-fatal failures.)
     */
    protected abstract void cook(Recipe recipe, ImportReportList reportList) throws IOException, InterruptedException;

    protected static InputStream read(XStreamDOM dom) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XStreamDOM.ConverterImpl c = new ConverterImpl();
        c.marshal(dom, new XppDriver().createWriter(baos), null);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
