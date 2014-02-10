package org.jenkinsci.plugins.recipe.ingredients.ViewIngredient;

def f = namespace(lib.FormTagLib)

f.entry(title:"View Name",field:"name") {
    f.textbox()
}
