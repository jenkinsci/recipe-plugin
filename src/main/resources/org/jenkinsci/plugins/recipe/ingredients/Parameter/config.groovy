package org.jenkinsci.plugins.recipe.ingredients.Parameter;

def f = namespace(lib.FormTagLib)

f.entry(title:my.name,field:my.name) {
    f.textbox()
}