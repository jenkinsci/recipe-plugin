package org.jenkinsci.plugins.recipe.Recipe

import org.jenkinsci.plugins.recipe.IngredientDescriptor;

def f = namespace(lib.FormTagLib)

f.entry(title:"Title",field:"title") {
    f.textbox()
}
f.entry(title:"Version",field:"version") {
    f.textbox(default:"1.0")
}
f.entry(title:"Description",field:"description") {
    f.textarea()
}
f.section(title:_("Ingredients")) {
    f.block {
        f.hetero_list(
            name:"ingredients",hasHeader:true, descriptors: IngredientDescriptor.all(),
            items:instance?.ingredients, addCaption:_("Add ingredient")
        )
    }
}
