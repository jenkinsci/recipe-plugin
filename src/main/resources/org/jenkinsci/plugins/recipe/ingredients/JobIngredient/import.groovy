package org.jenkinsci.plugins.recipe.ingredients.JobIngredient

import hudson.Util;

def f = namespace(lib.FormTagLib)

f.entry(title:"Job Name",field:"name") {
    f.textbox()
}
if (Util.fixEmpty(my.description)!=null) {
    f.entry(title:"Description",field:"description") {
        div(my.description)
    }
}
