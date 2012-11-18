package org.jenkinsci.plugins.recipe.ingredients.Parameter;

def f = namespace(lib.FormTagLib)

f.entry(title:"Job Name",field:"name") {
    f.textbox()
}
f.entry(title:"Description",field:"description") {
    f.textarea(readonly:true)
}