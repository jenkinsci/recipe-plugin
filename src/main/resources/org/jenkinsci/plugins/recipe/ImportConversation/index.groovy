package org.jenkinsci.plugins.recipe.ImportConversation;

def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)

l.layout {
    def title = _("Importing ${my.recipe.displayName}")
    l.header(title:title)
    l.main_panel {
        h1 title

        h3 _("Description")
        p my.recipe.description

        h3 _("Contents to be imported")
        f.form(action:"cook",method:"POST") {
            int n=0;
            my.recipe.ingredients.each { i ->
                f.section(name:"ingredient${n++}",title:i.descriptor.displayName) {
                    context.setVariable("instance",i)
                    context.setVariable("descriptor",i.descriptor)
                    include i,"import"
                }
            }
            f.block {
                f.submit(value:_("Import"))
            }
        }
    }
}