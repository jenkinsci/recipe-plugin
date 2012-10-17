package org.jenkinsci.plugins.recipe.ingredients.PluginIngredient;

def f = namespace(lib.FormTagLib)

f.entry(title:"Plugins",field:"names") {
    f.textbox(readonly:true)
}