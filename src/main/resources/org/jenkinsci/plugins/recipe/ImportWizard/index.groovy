package org.jenkinsci.plugins.recipe.ImportWizard;

def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)

l.layout {
    def title = _("Import Recipe")
    l.header(title:title)
    l.main_panel {
        h1 title

        f.form(action:"cook",method:"POST") {
            f.entry(title:_("Recipe URL")) {
                f.textbox()
            }
            f.block {
                f.submit(value:_("Retrieve Recipe"))
            }
        }
    }
}