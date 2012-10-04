package org.jenkinsci.plugins.recipe;

import hudson.Util;
import hudson.util.VariableResolver;
import hudson.util.VariableResolver.ByMap;
import jenkins.util.xstream.XStreamDOM;

import java.util.Map;

/**
 *
 * @author Kohsuke Kawaguchi
 */
public class ImportOptions {
    private final Map<String,String> variables;
    private final VariableResolver<String> resolver;

    public ImportOptions(Map<String, String> variables) {
        this.variables = variables;
        this.resolver = new ByMap<String>(variables);
    }

    public XStreamDOM apply(XStreamDOM dom) {
        return dom.expandMacro(resolver);
    }

    public String apply(String s) {
        return Util.replaceMacro(s,resolver);
    }
}
