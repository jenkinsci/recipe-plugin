package org.jenkinsci.plugins.recipe.ingredients.JobIngredient;

def f = namespace(lib.FormTagLib)

f.entry(title:"test",field:"value") {
    f.textbox()
}