package org.jenkinsci.plugins.recipe;

import org.jenkinsci.plugins.recipe.ingredients.PluginIngredient;
import org.jenkinsci.plugins.recipe.ingredients.PluginIngredient.Item;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Conversation-scoped object that guides the user through the importing process.
 *
 * Tied to {@link HttpSession}.
 *
 * @author Kohsuke Kawaguchi
 */
public class ImportConversation {
    /**
     * We are trying to import this recipe.
     */
    public final Recipe recipe;

    /**
     * If this conversation resulted in any error, put it here
     * and the user will see it.
     */
    public Exception error;

    public ImportReportList reportList = new ImportReportList();

    private boolean skipPlugin;

    public ImportConversation(Recipe recipe) {
        this.recipe = recipe;
        Stapler.getCurrentRequest().getSession().setAttribute(SESSION_KEY, this);
    }

    public static ImportConversation getCurrent() {
        return (ImportConversation)Stapler.getCurrentRequest().getSession().getAttribute(SESSION_KEY);
    }

    public List<PluginIngredient.Item> getPluginsThatRequireAttention() {
        List<PluginIngredient.Item> r = new ArrayList<Item>();
        if (skipPlugin)     return r;
        for (PluginIngredient p : recipe.getIngredients(PluginIngredient.class)) {
            for (Item i : p.parse()) {
                if (!i.isUpToDate())
                    r.add(i);
            }
        }
        return r;
    }

    public HttpResponse doApplyPlugins(StaplerRequest req) {
        if (req.hasParameter("skip")) {
            skipPlugin = true;
            return HttpResponses.redirectToDot();
        } else {
            Map<String,Item> r = new HashMap<String, Item>();
            for (Item i : getPluginsThatRequireAttention()) {
                r.put(i.name,i);
            }

            boolean change = false;
            for (String name : (Set<String>)req.getParameterMap().keySet()) {
                if (name.startsWith("plugin.")) {
                    Item i = r.remove(name.substring(7));
                    if (i!=null) {
                        i.installSource.deploy(true);
                        change = true;
                    }
                }
            }

            if (change)
                return HttpResponses.redirectViaContextPath("/updateCenter/");
            else {
                skipPlugin = true;
                return HttpResponses.redirectToDot();
            }
        }
    }

    public HttpResponse doCook(StaplerRequest req) throws ServletException {
        recipe.apply(req);

        // permission checked by individual Ingredients
        try {
            recipe.cook(reportList = new ImportReportList());
            return HttpResponses.redirectTo("report");
        } catch (IOException e) {
            error = e;
            return HttpResponses.redirectToDot();
        } catch (InterruptedException e) {
            error = e;
            return HttpResponses.redirectToDot();
        }
    }

    private static final String SESSION_KEY = ImportConversation.class.getName();
}
