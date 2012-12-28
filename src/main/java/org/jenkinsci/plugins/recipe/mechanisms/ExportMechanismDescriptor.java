package org.jenkinsci.plugins.recipe.mechanisms;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

/**
 * {@link Descriptor} for {@link ExportMechanism}.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ExportMechanismDescriptor extends Descriptor<ExportMechanism> {
    protected ExportMechanismDescriptor(Class<? extends ExportMechanism> clazz) {
        super(clazz);
    }

    protected ExportMechanismDescriptor() {
    }

    public static DescriptorExtensionList<ExportMechanism,ExportMechanismDescriptor> all() {
        return Jenkins.getInstance().getDescriptorList(ExportMechanism.class);
    }
}
