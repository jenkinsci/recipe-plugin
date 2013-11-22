package org.jenkinsci.plugins.recipe.ExportWizard

import org.jenkinsci.plugins.recipe.mechanisms.ExportMechanismDescriptor

def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)

l.layout {
    def title = _("Export Recipe")
    l.header(title:title)
    l.main_panel {
        h1 {
            img(src:rootURL + my.iconFileName)
            text(" "+title)
        }

        set("instance", my)
        set("descriptor", my.descriptor)

        f.form(action:"export",method:"POST") {
            f.section(title:_("Ingredients")) {
                f.property(field:"recipe")
            }

            f.section(title:_("Export to...")) {
                f.block {
                    f.hetero_radio(field:"mechanism",descriptors:ExportMechanismDescriptor.all())
                }
            }

            f.block { // TODO: switch to <f:bottomButtonBar>
                div(id:"bottom-sticker") {
                    div(class:"bottom-sticker-inner") {
                        f.submit(value:_("Export Recipe"))
                    }
                }
            }
        }
    }
}