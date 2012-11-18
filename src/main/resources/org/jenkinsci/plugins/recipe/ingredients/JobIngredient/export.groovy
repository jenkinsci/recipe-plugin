package org.jenkinsci.plugins.recipe.ingredients.JobIngredient;

def f = namespace(lib.FormTagLib)

f.entry(title:"Job Name",field:"name") {
    f.textbox()
}