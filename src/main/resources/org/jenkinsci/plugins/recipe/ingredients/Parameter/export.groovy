package org.jenkinsci.plugins.recipe.ingredients.Parameter;

def f = namespace(lib.FormTagLib)

f.entry(title:"test",field:"value") {
    f.textbox()
}