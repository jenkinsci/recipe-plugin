package org.jenkinsci.plugins.recipe.ingredients;

import hudson.model.Job;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import jenkins.util.xstream.XStreamDOM;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jenkinsci.plugins.recipe.ImportOptions;
import org.jenkinsci.plugins.recipe.Ingredient;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public class JobIngredient extends Ingredient {
    private String name;
    private XStreamDOM definition;

    public JobIngredient(String name, XStreamDOM definition) {
        this.name = name;
        this.definition = definition;
    }

    public String getName() {
        return name;
    }

    public XStreamDOM getDefinition() {
        return definition;
    }

    @Override
    public JobIngredient apply(ImportOptions opts) {
        return new JobIngredient(opts.apply(name),opts.apply(definition));
    }

    @Override
    public void cook() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XStream2 xs = new XStream2();
        xs.toXML(definition,baos);
        Jenkins.getInstance().createProjectFromXML(name,new ByteArrayInputStream(baos.toByteArray()));
    }

    public static JobIngredient fromJob(Job j) {
        XStreamDOM dom = XStreamDOM.from(j.getConfigFile().getXStream(),j);
        return new JobIngredient(j.getName(),dom);
    }
}
