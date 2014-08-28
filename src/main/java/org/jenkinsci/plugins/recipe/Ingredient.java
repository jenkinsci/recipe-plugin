package org.jenkinsci.plugins.recipe;

import com.google.common.base.Charsets;
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
import java.io.OutputStreamWriter;
import java.io.Writer;

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

    /**
     * Allows a DOM tree to be read as an XML bytestream.
     * Explicitly specifies UTF-8 encoding.
     */
    protected static InputStream read(XStreamDOM dom) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("<?xml version='1.0' encoding='UTF-8'?>\n".getBytes());
        XStreamDOM.ConverterImpl c = new ConverterImpl();
        Writer w = new OutputStreamWriter(baos, Charsets.UTF_8);
        try {
            c.marshal(dom, new XppDriver().createWriter(w), null);
        } finally {
            w.close();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
