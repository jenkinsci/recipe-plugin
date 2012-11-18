package org.jenkinsci.plugins.recipe.ingredients.PluginIngredient;

def f = namespace(lib.FormTagLib)

// TODO: better to show what plugins are to be installed/updated
f.entry(title:"Plugins",field:"names") {
    f.textbox(readonly:true)
}