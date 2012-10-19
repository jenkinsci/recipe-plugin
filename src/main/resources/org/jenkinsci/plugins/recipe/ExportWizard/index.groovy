package org.jenkinsci.plugins.recipe.ExportWizard

import org.jenkinsci.plugins.recipe.Recipe;

def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)

l.layout {
    def title = _("Export Recipe")
    l.header(title:title)
    l.main_panel {
        h1 title

        context.setVariable("instance",null)
        def d = app.getDescriptor(Recipe.class)
        context.setVariable("descriptor", d)

        f.form(action:"export",method:"POST") {
            include(d.clazz,d.configPage)
            f.block {
                div(style:"margin-top:1em")
                f.submit(value:_("Export Recipe"))
            }
        }
    }
}