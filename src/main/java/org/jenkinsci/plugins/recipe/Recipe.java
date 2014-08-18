package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.Util;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.IOUtils;
import hudson.util.VariableResolver;
import hudson.util.VersionNumber;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import jenkins.util.xstream.XStreamDOM;
import net.sf.json.JSONObject;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jenkinsci.plugins.recipe.ingredients.Parameter;
import org.jenkinsci.plugins.recipe.ingredients.PluginIngredient;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Group of {@link Ingredient}s that represent a set of configured stuff
 * in Jenkins that gets transported from one Jenkins to another,
 * for example for one person to share how he set up something to other people.
 *
 * @author Kohsuke Kawaguchi
 */
public class Recipe extends AbstractDescribableImpl<Recipe> implements HttpResponse {
    private String id;
    private String version;
    private String displayName;
    private String description;
    private List<Ingredient> ingredients = new ArrayList<Ingredient>();

    @DataBoundConstructor
    public Recipe(String id, String version, String displayName, String description, List<? extends Ingredient> ingredients) {
        setId(id);
        this.version = version;
        this.displayName = displayName;
        this.description = description;
        this.ingredients.addAll(Util.fixNull(ingredients));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (ID_PATTERN.matcher(id).matches())
            this.id = id;
        else
            throw new IllegalArgumentException("Invalid ID: "+id);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the suggested file name for this recipe.
     */
    public String getFileName() {
        return id+EXTENSION;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Parameter> getParameters() {
        return getIngredients(Parameter.class);
    }

    public <T extends Ingredient> List<T> getIngredients(Class<T> type) {
        return Util.filter(ingredients,type);
    }

    public void writeTo(OutputStream out) throws IOException {
        XSTREAM.toXML(this,out);
    }

    public void writeTo(Writer out) throws IOException {
        XSTREAM.toXML(this,out);
    }

    /**
     * Writes this object as HTTP response.
     */
    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
        rsp.setContentType("application/xml;charset=UTF-8");
        Recipe.XSTREAM.toXMLUTF8(this,rsp.getOutputStream());
    }

    public void apply(StaplerRequest req) throws ServletException {
        JSONObject structure = req.getSubmittedForm();
        for (int i=0; i<getIngredients().size(); i++) {
            getIngredients().get(i).apply(req,structure.getJSONObject("ingredient"+i));
        }
    }

    /**
     * Imports this ingredient into the current Jenkins.
     */
    public void cook(ImportReportList reportList) throws IOException, InterruptedException {
        for (Ingredient i : getIngredients())
            i.cook(this, reportList);
    }

    public ImportOptions createImportOptions() {
        return new ImportOptions();
    }

    public final class ImportOptions implements VariableResolver<String> {
        private final VariableResolver<String> resolver;

        private ImportOptions() {
            Map<String, String> variables = new HashMap<String, String>();
            for (Parameter p : getParameters())
                variables.put(p.name,p.getValue());
            this.resolver = new ByMap<String>(variables);
        }

        public String resolve(String s) {
            return resolver.resolve(s);
        }

        public XStreamDOM apply(XStreamDOM dom) {
            return dom.expandMacro(resolver);
        }

        public String apply(String s) {
            return Util.replaceMacro(s,resolver);
        }
    }

    /**
     * Loads the recipe from URL.
     */
    public static Recipe load(URL url) throws IOException {
        byte[] payload = IOUtils.toByteArray(url.openStream());

        // look at the plugin designations and internalize them via PluginIngredient
        Recipe recipe = (Recipe) XSTREAM.fromXML(new ByteArrayInputStream(payload));
        Map<String, VersionNumber> map = org.jenkinsci.plugins.recipe.Util.parseRequestedPlugins(new ByteArrayInputStream(payload));
        if (!map.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            for (Entry<String, VersionNumber> e : map.entrySet()) {
                if (buf.length()>0) buf.append(',');
                buf.append(e.getKey()).append('@').append(e.getValue());
            }
            PluginIngredient pi = new PluginIngredient(buf.toString());
            recipe.getIngredients().add(pi);
        }

        return recipe;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Recipe> {
        @Override
        public String getDisplayName() {
            return "";
        }

        public FormValidation doCheckVersion(@QueryParameter String value) {
            try {
                new VersionNumber(value);
                return FormValidation.ok();
            } catch (Exception e) {
                return FormValidation.error(value+" is not a valid version number");
            }
        }
    }

    public static XStream2 XSTREAM = new XStream2();

    @Initializer(after=InitMilestone.PLUGINS_STARTED)
    public static void init() {
        XSTREAM.alias("recipe",Recipe.class);
        for (IngredientDescriptor d : IngredientDescriptor.all())
            XSTREAM.alias(d.getPersistenceElementName(),d.clazz);
    }

    /**
     * Common file extension for Jenkins recipe.
     */
    public static final String EXTENSION = ".jrcp";

    public static final Pattern ID_PATTERN = Pattern.compile("[A-Za-z0-9\\-._]+");
}
