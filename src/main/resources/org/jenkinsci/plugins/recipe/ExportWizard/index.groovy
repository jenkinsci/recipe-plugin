package org.jenkinsci.plugins.recipe.ExportWizard

import org.jenkinsci.plugins.recipe.Recipe
import org.jenkinsci.plugins.recipe.mechanisms.ExportMechanismDescriptor;

def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)

l.layout {
    def title = _("Export Recipe")
    l.header(title:title)
    l.main_panel {
        h1 title

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
//
//                // basically emulating /lib/hudson/newFromList except the name field.
//                ExportMechanismDescriptor.all().each { ed ->
//                    f.block {
//                        label {
//                            input(type:"radio",name:"mode",value:ed.id)
//                            b(ed.displayName)
//                        }
//                    }
//                    f.entry {
//                        set("instance",ed);
//                        include(ed,"newInstanceDetail");
//                    }
//                }
            }

            f.block { // TODO: switch to <f:bottomButtonBar>
                div(style:"margin-top:1em")
                f.submit(value:_("Export Recipe"))
            }
        }
    }
}