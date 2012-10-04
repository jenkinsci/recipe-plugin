package org.jenkinsci.plugins.recipe.ImportConversation;

def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)

l.layout {
    def title = _("Importing ${my.recipe.title}")
    l.header(title:title)
    l.main_panel {
        h1 title

        h3 _("Description")
        p my.recipe.description

        h3 _("Import Parameters")
        f.form(action:"cook",method:"POST") {
            my.recipe.parameters.each { p ->
                include p,"config"
            }
            f.block {
                f.submit(value:_("Import"))
            }
        }
    }
}