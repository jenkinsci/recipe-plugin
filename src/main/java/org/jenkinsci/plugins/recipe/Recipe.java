package org.jenkinsci.plugins.recipe;

import hudson.Extension;
import hudson.Util;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.VariableResolver;
import hudson.util.VersionNumber;
import hudson.util.XStream2;
import jenkins.util.xstream.XStreamDOM;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.recipe.ingredients.Parameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kohsuke Kawaguchi
 */
public class Recipe extends AbstractDescribableImpl<Recipe> {
    private String version;
    private String title;
    private String description;
    private List<Ingredient> ingredients = new ArrayList<Ingredient>();

    @DataBoundConstructor
    public Recipe(String version, String title, String description, List<Ingredient> ingredients) {
        this.version = version;
        this.title = title;
        this.description = description;
        this.ingredients.addAll(ingredients);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        return Util.filter(ingredients,Parameter.class);
    }

    public void apply(StaplerRequest req) throws ServletException {
        JSONObject structure = req.getSubmittedForm();
        for (int i=0; i<getIngredients().size(); i++) {
            getIngredients().get(i).apply(req,structure.getJSONObject("ingredient"+i));
        }
    }

    public void cook() throws IOException, InterruptedException {
        for (Ingredient i : getIngredients())
            i.cook(this, 0);
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
        return (Recipe)XSTREAM.fromXML(url.openStream());
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
}
