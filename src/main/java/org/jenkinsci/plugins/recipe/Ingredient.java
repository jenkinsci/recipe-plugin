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
 * @author Kohsuke Kawaguchi
 */
public abstract class Ingredient extends AbstractDescribableImpl<Ingredient> implements ExtensionPoint {
    @Override
    public IngredientDescriptor getDescriptor() {
        return (IngredientDescriptor)super.getDescriptor();
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
