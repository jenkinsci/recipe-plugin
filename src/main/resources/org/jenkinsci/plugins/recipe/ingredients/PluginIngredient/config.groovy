package org.jenkinsci.plugins.recipe.ingredients.PluginIngredient;

def f = namespace(lib.FormTagLib)
def st = namespace("jelly:stapler")

f.entry(title:"Plugin Name(s)",field:"names",description:_("description")) {
    st.adjunct(includes:"org.jenkinsci.plugins.chosen.chosen")
    f.select(clazz:"chosen",multiple:true)
}